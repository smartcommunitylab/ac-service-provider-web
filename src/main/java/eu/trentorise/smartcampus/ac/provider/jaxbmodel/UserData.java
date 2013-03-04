/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package eu.trentorise.smartcampus.ac.provider.jaxbmodel;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import eu.trentorise.smartcampus.ac.provider.model.Attribute;

/**
 * @author raman
 *
 */
@XmlRootElement(name = "UserData")
public class UserData {

	private String userId;
	private long socialId;
	
	private List<Attribute> identityAttributes;

	private String token;
	private long expires;
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the socialId
	 */
	public long getSocialId() {
		return socialId;
	}

	/**
	 * @param socialId the socialId to set
	 */
	public void setSocialId(long socialId) {
		this.socialId = socialId;
	}

	/**
	 * @return the identityAttributes
	 */
	public List<Attribute> getIdentityAttributes() {
		return identityAttributes;
	}

	/**
	 * @param identityAttributes the identityAttributes to set
	 */
	public void setIdentityAttributes(List<Attribute> identityAttributes) {
		this.identityAttributes = identityAttributes;
	}	

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the expires
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(long expires) {
		this.expires = expires;
	}
	
}
