/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.kisanhub.intellij.useful.commandLine.Argument.*;
import static java.lang.Boolean.TRUE;
import static java.lang.System.arraycopy;
import static java.lang.System.setProperty;
import static java.lang.Thread.currentThread;

public final class ExternalDriver
{
	@SuppressWarnings("HardcodedFileSeparator")
	@NonNls
	@NotNull
	public static final String MacOsXHomePath = "/Applications/IntelliJ IDEA 14 CE.app/Contents";

	@NotNull
	@NonNls
	private static final String JavaAwtHeadless = "java.awt.headless";

	@SuppressWarnings("ConstantConditions")
	@NotNull
	@NonNls
	private static final String BooleanTrueString = TRUE.toString();

	@NotNull
	private static final Object Empty = new String[0];

	@NotNull
	@NonNls
	private static final String MainImplReplacement = "com.intellij.idea.MainImplReplacement";

	// com.intellij.openapi.application.PathManager.PROPERTY_HOME_PATH
	@NotNull
	@NonNls
	private static final String PROPERTY_HOME_PATH = "idea.home.path";

	// com.intellij.openapi.application.PathManager.LIB_FOLDER
	@NotNull
	@NonNls
	private static final String LIB_FOLDER = "lib";

	@NotNull
	@NonNls
	private final File homePath;

	@NotNull
	private final ClassLoader intelliJClassPath;

	public ExternalDriver(@NotNull final File homePath)
	{
		this.homePath = homePath;

		intelliJClassPath = createIntelliJClassPath();
	}

	public void invoke(@NotNull final String abstractCommandLineApplicationStarterExClassName, @NotNull final String... commandLineArguments)
	{
		forceIntelliJHome();
		forceIntelliJToBeHeadless();
		forceIntelliJToBeInCommandLineMode();
		invokeIntelliJWithOurApplication(intelliJCommandLineArguments(abstractCommandLineApplicationStarterExClassName, commandLineArguments));
	}

	@NotNull
	private static String[] intelliJCommandLineArguments(@NotNull final String abstractCommandLineApplicationStarterExClassName, @NotNull final String... commandLineArguments)
	{
		final int length = commandLineArguments.length;
		final String[] intelliJCommandLineArguments = new String[length + 1];
		intelliJCommandLineArguments[0] = abstractCommandLineApplicationStarterExClassName;
		arraycopy(commandLineArguments, 0, intelliJCommandLineArguments, 1, length);
		return intelliJCommandLineArguments;
	}

	@SuppressWarnings("AccessOfSystemProperties")
	private void forceIntelliJHome()
	{
		setProperty(PROPERTY_HOME_PATH, homePath.getPath());
	}

	@SuppressWarnings("AccessOfSystemProperties")
	private static void forceIntelliJToBeHeadless()
	{
		setProperty(JavaAwtHeadless, BooleanTrueString);
	}

	@SuppressWarnings("ClassLoaderInstantiation")
	@NotNull
	private ClassLoader createIntelliJClassPath()
	{
		final Thread currentThread = currentThread();
		final File intelliJJarPath = new File(homePath, LIB_FOLDER);
		final JarFileClassLoader jarFileClassLoader = new JarFileClassLoader(currentThread.getContextClassLoader(), intelliJJarPath);
		currentThread.setContextClassLoader(jarFileClassLoader);
		return jarFileClassLoader;
	}

	// Done this way because of ClassLoader issues
	private void forceIntelliJToBeInCommandLineMode()
	{
		// setFlags(Empty);
		executeStaticVoidMethod("com.intellij.idea.Main", "setFlags", argument(String[].class, Empty));
	}

	// Done this way because of ClassLoader issues
	@SuppressWarnings({"StringConcatenationMissingWhitespace", "UseOfSystemOutOrSystemErr", "CallToSystemExit", "ConstantConditions"})
	private void invokeIntelliJWithOurApplication(@NotNull final String... intelliJCommandLineArguments)
	{
		// main(intelliJCommandLineArguments, MainImplReplacement, "start");
		executeStaticVoidMethod("com.intellij.ide.Bootstrap", "main", argument(intelliJCommandLineArguments), argument(MainImplReplacement), argument("start"));
	}

	private void executeStaticVoidMethod(@NotNull @NonNls final String className, @NonNls @NotNull final String name, @NotNull final Argument... arguments)
	{
		final Class<?> intelliJClass = loadIntelliJClass(className);
		final Method intelliJClassMethod = getIntelliJClassMethod(intelliJClass, name, toParameterTypes(arguments));
		invokeStaticVoidMethod(intelliJClassMethod, toValues(arguments));
	}

	@NotNull
	private Class<?> loadIntelliJClass(@NotNull @NonNls final String className)
	{
		final Class<?> intelliJClass;
		try
		{
			intelliJClass = intelliJClassPath.loadClass(className);
		}
		catch (final ClassNotFoundException e)
		{
			throw new IllegalStateException(e);
		}
		assert intelliJClass != null;
		return intelliJClass;
	}

	@NotNull
	private static Method getIntelliJClassMethod(@NotNull final Class<?> intelliJClass, @NonNls @NotNull final String name, @NotNull final Class<?>... parameterTypes)
	{
		final Method method;
		try
		{
			method = intelliJClass.getDeclaredMethod(name, parameterTypes);
		}
		catch (final NoSuchMethodException e)
		{
			throw new IllegalStateException(e);
		}
		assert method != null;
		method.setAccessible(true);
		return method;
	}

	private static void invokeStaticVoidMethod(@NotNull final Method method, @NotNull final Object... arguments)
	{
		try
		{
			method.invoke(null, arguments);
		}
		catch (final IllegalAccessException e)
		{
			throw new IllegalStateException(e);
		}
		catch (final InvocationTargetException e)
		{
			//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw new IllegalStateException(e.getCause());
		}
	}
}
