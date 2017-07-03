import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class Launcher {

	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		launcher.findPrograms();
		launcher.display();
		int program = launcher.input();
		launcher.execute(program);
	}

	Class[] programs;

	public void findPrograms() {
		List result = new ArrayList();
		File currentDirectory = new File(".");
		String[] files = currentDirectory.list();
		try {
			System.out.println(currentDirectory.getCanonicalPath());
		} catch (Exception e) {
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".class")) {
				Class program = getProgram(files[i]);
				if (program != null)
					result.add(program);
			}
		}
		programs = (Class[]) result.toArray(new Class[result.size()]);
	}

	public Class getProgram(String name) {
		try {
			String className = name.substring(0, name.indexOf(".class"));
			Class programClass = Class.forName(className);
			if (programClass
					.getDeclaredMethod("main", new Class[] { String[].class }) != null)
				return programClass;
		} catch (Exception e) {
		}
		return null;
	}

	public void display() {
		System.out.println("--- Launcher ---");
		for (int i = 0; i < programs.length; i++) {
			System.out.println(i + ". " + programs[i].getName());
		}
	}

	public int input() {
		System.out.println();
		System.out.println("Please enter program number to execute: ");
		Scanner keyboard = new Scanner(System.in);
		String input = keyboard.nextLine();
		return Integer.parseInt(input);
	}

	public void execute(int program) {
		if (program < 0 | program > programs.length)
			return;
		try {
			Method main = programs[program].getDeclaredMethod("main",
					new Class[] { String[].class });
			Object[] args = new Object[1];
			args[0] = new String[0];
			main.invoke(null, args);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}