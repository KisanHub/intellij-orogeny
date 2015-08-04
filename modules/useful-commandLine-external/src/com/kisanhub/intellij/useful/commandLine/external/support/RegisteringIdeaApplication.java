/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine.external.support;

import com.intellij.idea.IdeaApplication;
import com.intellij.openapi.application.ApplicationStarter;
import com.intellij.openapi.extensions.ExtensionPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.intellij.ExtensionPoints.APPLICATION_STARTER;
import static com.intellij.ide.plugins.PluginManagerCore.getPlugins;
import static com.intellij.openapi.extensions.Extensions.getRootArea;
import static java.lang.Class.forName;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class RegisteringIdeaApplication extends IdeaApplication
{
	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public RegisteringIdeaApplication(@NotNull final String... commandLineArguments)
	{
		super(commandLineArguments);
	}

	// Called from superclass constructor
	@NotNull
	@Override
	public ApplicationStarter getStarter()
	{
		final String[] commandLineArguments = getCommandLineArguments();
		assert commandLineArguments != null;

		final String commandName = commandLineArguments[0];
		assert commandName != null;

		final ClassLoader classLoader = getClassLoader();
		final ApplicationStarter ourApplication = createApplicationStarter(commandName, classLoader);
		registerApplicationStarter(ourApplication);

		return super.getStarter();
	}

	@SuppressWarnings("MethodOnlyUsedFromInnerClass")
	private static void registerApplicationStarter(@NotNull final ApplicationStarter applicationStarter)
	{
		// When this is called, it wipes out the registration of all ExtensionPoints
		getPlugins();

		final ExtensionPoint<ApplicationStarter> extensionPoint = getRootArea().getExtensionPoint(APPLICATION_STARTER);
		// Do not register if already loaded as a plugin
		if (extensionPoint.hasExtension(applicationStarter))
		{
			return;
		}
		extensionPoint.registerExtension(applicationStarter);
	}

	@NotNull
	@SuppressWarnings({"unchecked", "MethodOnlyUsedFromInnerClass"})
	private static ApplicationStarter createApplicationStarter(@NotNull final String className, @Nullable final ClassLoader classLoader)
	{
		final Class<? extends ApplicationStarter> clazz;
		try
		{
			clazz = (Class<? extends ApplicationStarter>) forName(className, true, classLoader);
		}
		catch (final ClassNotFoundException e)
		{
			throw new IllegalStateException(format(ENGLISH, "Could not load class '%1$s'", className), e); //NON-NLS
		}
		assert clazz != null;

		final Constructor<? extends ApplicationStarter> constructor;
		try
		{
			constructor = clazz.getConstructor();
		}
		catch (final NoSuchMethodException e)
		{
			throw new IllegalStateException(couldNotInstantiateClass(className), e);
		}

		try
		{
			assert constructor != null;
			return constructor.newInstance();
		}
		catch (final IllegalAccessException e)
		{
			throw new IllegalStateException(couldNotInstantiateClass(className), e);
		}
		catch (final InstantiationException e)
		{
			throw new IllegalStateException(couldNotInstantiateClass(className), e);
		}
		catch (final InvocationTargetException e)
		{
			throw new IllegalStateException(couldNotInstantiateClass(className), e);
		}
	}

	@NotNull
	private ClassLoader getClassLoader()
	{
		final Class<? extends RegisteringIdeaApplication> mainImplClass = getClass();
		assert mainImplClass != null;
		final ClassLoader classLoader = mainImplClass.getClassLoader();
		assert classLoader != null;
		return classLoader;
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@NotNull
	private static String couldNotInstantiateClass(@NotNull final String className)
	{
		final String format = format(ENGLISH, "Could not instantiate class '%1$s'", className); // NON-NLS
		assert format != null;
		return format;
	}
}
