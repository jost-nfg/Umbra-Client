/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.managers.altmanager;

public class XboxLiveToken {
	private final String token;
	private final String hash;

	/**
	 * Constructor for an Xbox Live Token
	 * 
	 * @param token
	 * @param uhs
	 */
	public XboxLiveToken(String token, String uhs) {
		this.token = token;
		hash = uhs;
	}

	/**
	 * Returns the token of the Xbox Live Token.
	 * 
	 * @return The token of the Xbox Live Token.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Returns the hash of the Xbox Live Token.
	 * 
	 * @return The hash of the Xbox Live Token.
	 */
	public String getHash() {
		return hash;
	}
}
