package net.umbra.rendering.shaders;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;

import net.umbra.rendering.RenderApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class ShaderManager {
	private static final Logger LOGGER = LogUtils.getLogger();

	// Defines the built in shaders.
	private static final String BUILTIN_SHADER_PATH = "/assets/umbra/shaders/";
	private static final String[] BUILTIN_SHADER_IDS = { "rainbow", "blur", "blur_gradient", "gradient",
			"radial_gradient", "conical_gradient", "diamond_gradient", "image" };

	private final Map<String, Shader> shaders = new LinkedHashMap<>();

	public ShaderManager() {
		// Build default pipelines (fail-soft: if the GPU backend rejects pipeline
		// registration, e.g. under an unexpected render backend, keep the manager
		// alive with whatever shaders could be built instead of crashing the client).
		try {
			RenderPipeline solidPipeline2D = buildShaderPipeline2D("solid");
			RenderPipeline solidPipeline3D = buildShaderPipeline3D("solid");
			Shader solidShader = new Shader("solid", "Solid", "Solid color", RenderApi.current(), solidPipeline2D,
					solidPipeline3D, List.of(ShaderUniform.ofColor("Base Color", 1f, 1f, 1f, 1f)));
			shaders.put("solid", solidShader);
		} catch (Throwable t) {
			LOGGER.error("[Umbra] Failed to build solid shader pipelines", t);
		}

		// Load built-in shaders from resource JSON files
		for (String id : BUILTIN_SHADER_IDS) {
			String jsonPath = BUILTIN_SHADER_PATH + "umbra_" + id + ".json";
			try (InputStream is = ShaderManager.class.getResourceAsStream(jsonPath)) {
				if (is == null) {
					LOGGER.warn("[Umbra] Built-in shader JSON not found: {}", jsonPath);
					continue;
				}
				JsonObject json = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
				Shader shader = parseShader(id, json);
				if (shader != null) {
					shaders.put(id, shader);
					LOGGER.info("[Umbra] Loaded built-in shader: {}", shader.name());
				}
			} catch (Exception e) {
				LOGGER.error("[Umbra] Failed to load built-in shader: {}", id, e);
			}
		}

		// Load user shaders from /umbra/shaders/
		loadUserShaders();
	}

	/**
	 * Loads all of the user shaders from /umbra/shaders/
	 */
	private void loadUserShaders() {
		Path shadersDir = Minecraft.getInstance().gameDirectory.toPath().resolve("umbra/shaders");
		if (!Files.isDirectory(shadersDir))
			return;

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(shadersDir, "*.json")) {
			for (Path jsonFile : stream) {
				String fileName = jsonFile.getFileName().toString();
				String id = fileName.substring(0, fileName.length() - 5);

				if (shaders.containsKey(id)) {
					LOGGER.warn("[Umbra] User shader '{}' conflicts with existing shader, skipping", id);
					continue;
				}

				try {
					String jsonStr = Files.readString(jsonFile, StandardCharsets.UTF_8);
					JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
					Shader shader = parseShader(id, json);
					if (shader != null) {
						shaders.put(id, shader);
						LOGGER.info("[Umbra] Loaded user shader: {}", shader.name());
					}
				} catch (Exception e) {
					LOGGER.error("[Umbra] Failed to load user shader: {}", id, e);
				}
			}
		} catch (Exception e) {
			LOGGER.error("[Umbra] Failed to scan user shaders directory", e);
		}
	}

	/**
	 * Parses json representing the definition of a Shader and returns a built Shader object.
	 * @param id ID of the shader
	 * @param json Json representing the definition of the shader
	 * @return Resulting Shader parsed from the json.
	 */
	private Shader parseShader(String id, JsonObject json) {
		String name = json.has("name") ? json.get("name").getAsString() : id;
		String description = json.has("description") ? json.get("description").getAsString() : "";

		// Get the render backend that the shader is compatible with.
		RenderApi backend = RenderApi.OPENGL;
		if (json.has("backend")) {
			try {
				backend = RenderApi.valueOf(json.get("backend").getAsString().toUpperCase());
			} catch (IllegalArgumentException e) {
				LOGGER.warn("[Umbra] Unknown backend '{}' for shader '{}', defaulting to OPENGL",
						json.get("backend").getAsString(), id);
			}
		}

		// Gets the list of parameters of the shader.
		List<ShaderUniform> params = new ArrayList<>();
		if (json.has("params")) {
			JsonArray paramsArray = json.getAsJsonArray("params");
			for (JsonElement elem : paramsArray) {
				JsonObject p = elem.getAsJsonObject();
				ShaderUniform param = parseParam(p);
				if (param != null)
					params.add(param);
			}
		}

		// Creates a pipeline for both 3D and 2D.
		RenderPipeline pipeline2D = buildShaderPipeline2D(id);
		RenderPipeline pipeline3D = buildShaderPipeline3D(id);
		return new Shader(id, name, description, backend, pipeline2D, pipeline3D, params);
	}

	private ShaderUniform parseParam(JsonObject p) {
		String type = p.get("type").getAsString().toLowerCase();

		if (type.equals("time")) {
			return ShaderUniform.ofTime();
		} else if (type.equals("resolution")) {
			return ShaderUniform.ofResolution();
		}

		String name = p.get("name").getAsString();
		if (type.equals("float")) {
			float def = p.get("default").getAsFloat();
			float min = p.has("min") ? p.get("min").getAsFloat() : 0f;
			float max = p.has("max") ? p.get("max").getAsFloat() : 1f;
			float step = p.has("step") ? p.get("step").getAsFloat() : 0.1f;
			return ShaderUniform.ofFloat(name, def, min, max, step);
		} else if (type.equals("color")) {
			JsonArray def = p.getAsJsonArray("default");
			float r = def.get(0).getAsFloat();
			float g = def.get(1).getAsFloat();
			float b = def.get(2).getAsFloat();
			float a = def.size() > 3 ? def.get(3).getAsFloat() : 1.0f;
			return ShaderUniform.ofColor(name, r, g, b, a);
		} else if (type.equals("framebuffer")) {
			return ShaderUniform.ofFramebuffer(name);
		}

		LOGGER.warn("[Umbra] Unknown param type '{}' for param '{}'", type, name);
		return null;
	}

	private static Identifier umbraShader(String name) {
		return Identifier.fromNamespaceAndPath("umbra", "umbra_" + name);
	}

	// TODO: Can we somehow make these one?
	private static RenderPipeline buildShaderPipeline2D(String id) {
		return RenderPipelines.register(RenderPipeline.builder().withVertexShader(umbraShader(""))
				.withFragmentShader(umbraShader(id))
				.withBindGroupLayout(BindGroupLayout.builder()
						.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
						.withUniform("Projection", UniformType.UNIFORM_BUFFER)
						.withUniform("UmbraShaderParams", UniformType.UNIFORM_BUFFER)
						.withSampler("Sampler0").build())
				.withCull(false)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
				.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
				.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
				.withLocation(Identifier.fromNamespaceAndPath("umbra", "pipeline/umbra_" + id)).build());
	}

	private static RenderPipeline buildShaderPipeline3D(String id) {
		return RenderPipelines.register(RenderPipeline.builder().withVertexShader(umbraShader(""))
				.withFragmentShader(umbraShader(id))
				.withBindGroupLayout(BindGroupLayout.builder()
						.withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
						.withUniform("Projection", UniformType.UNIFORM_BUFFER)
						.withUniform("UmbraShaderParams", UniformType.UNIFORM_BUFFER)
						.withSampler("Sampler0").build())
				.withCull(false)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
				.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
				.withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
				.withLocation(Identifier.fromNamespaceAndPath("umbra", "pipeline/umbra_3d_" + id)).build());
	}

	/**
	 * Gets a shader with a given id.
	 * @param id Id of the shader to get.
	 * @return Shader if one is found.
	 */
	public Shader getShader(String id) {
		return shaders.get(id);
	}

	/**
	 * Returns all loaded shaders.
	 */
	public List<Shader> getAvailableShaders() {
		return Collections.unmodifiableList(new ArrayList<>(shaders.values()));
	}
}
