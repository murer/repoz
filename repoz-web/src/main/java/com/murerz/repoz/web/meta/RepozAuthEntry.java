package com.murerz.repoz.web.meta;

public class RepozAuthEntry implements Comparable<RepozAuthEntry> {

	private final String path;
	private final String user;

	public RepozAuthEntry(String path, String user) {
		if(path == null) {
			throw new RuntimeException("path is required");
		}
		if(user == null) {
			throw new RuntimeException("user is required");
		}
		this.path = path;
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepozAuthEntry other = (RepozAuthEntry) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public int compareTo(RepozAuthEntry o) {
		if (this == o) {
			return 0;
		}
		int ret = this.path.compareTo(o.path);
		if (ret == 0) {
			ret = this.user.compareTo(o.user);
		}
		return ret;
	}

}
