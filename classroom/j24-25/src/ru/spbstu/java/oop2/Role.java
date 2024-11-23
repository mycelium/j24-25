package ru.spbstu.java.oop2;

public enum Role {
	ADMIN(0),
	USER(1),
	GUEST(2);
	
	private int roleId;
	
	private Role(int roleId) {
		this.roleId = roleId;
	}

	public int getId() {
		return roleId;
	}
	
	public static Role getById(int id) {
		Role result = null;
		switch (id) {
		case 0: {
			result = ADMIN;
			break;
		}
		case 1: {
			result = USER;
			break;
		}
		case 2: {
			result = GUEST;
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + id);
		}
		return result;
	}
}
