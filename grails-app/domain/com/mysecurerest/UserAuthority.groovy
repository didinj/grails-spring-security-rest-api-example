package com.mysecurerest

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.apache.commons.lang.builder.HashCodeBuilder

@ToString(cache=true, includeNames=true, includePackage=false)
class UserAuthority implements Serializable {

	private static final long serialVersionUID = 1

	User user
	Authority authority

	@Override
	boolean equals(other) {
		if (other instanceof UserAuthority) {
			other.userId == user?.id && other.authorityId == authority?.id
		}
	}

	@Override
	int hashCode() {
		def builder = new HashCodeBuilder()
		if (user) builder.append(user.id)
		if (authority) builder.append(authority.id)
		builder.toHashCode()
	}

	static UserAuthority get(long userId, long authorityId) {
		criteriaFor(userId, authorityId).get()
	}

	static boolean exists(long userId, long authorityId) {
		criteriaFor(userId, authorityId).count()
	}

	private static DetachedCriteria criteriaFor(long userId, long authorityId) {
		UserAuthority.where {
			user == User.load(userId) &&
			authority == Authority.load(authorityId)
		}
	}

	static UserAuthority create(User user, Authority authority) {
		def instance = new UserAuthority(user: user, authority: authority)
		instance.save()
		instance
	}

	static boolean remove(User u, Authority r) {
		if (u != null && r != null) {
			UserAuthority.where { user == u && authority == r }.deleteAll()
		}
	}

	static int removeAll(User u) {
		u == null ? 0 : UserAuthority.where { user == u }.deleteAll()
	}

	static int removeAll(Authority r) {
		r == null ? 0 : UserAuthority.where { authority == r }.deleteAll()
	}

	static constraints = {
		authority validator: { Authority r, UserAuthority ur ->
			if (ur.user?.id) {
				UserAuthority.withNewSession {
					if (UserAuthority.exists(ur.user.id, r.id)) {
						return ['userRole.exists']
					}
				}
			}
		}
	}

	static mapping = {
		id composite: ['user', 'authority']
		version false
	}
}
