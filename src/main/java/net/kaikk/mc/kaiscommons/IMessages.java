package net.kaikk.mc.kaiscommons;

public interface IMessages {
	String get(String id);
	String get(String id, String... replacements);
}
