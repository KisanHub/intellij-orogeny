/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public final class JarFileClassLoader extends URLClassLoader
{
	@NotNull
	@NonNls
	private static final String jar = ".jar";

	@NotNull
	private static final URL[] EmptyUrls = {};

	public JarFileClassLoader(@Nullable final ClassLoader parentClassLoader)
	{
		super(EmptyUrls, parentClassLoader);
	}

	public JarFileClassLoader(@Nullable final ClassLoader parentClassLoader, @NotNull final File folderPath)
	{
		this(parentClassLoader);
		addJarFilesInPath(folderPath);
	}

	public void addJarFilesInPath(@NotNull final File folderPath)
	{
		final File[] potentialJarFiles = folderPath.listFiles();
		assert potentialJarFiles != null;
		for (final File potentialJarFile : potentialJarFiles)
		{
			addJarFile(potentialJarFile);
		}
	}

	public void addJarFile(@NotNull final File potentialJarFile)
	{
		if (!potentialJarFile.getName().endsWith(jar))
		{
			return;
		}

		addURL(fileToJarUrl(potentialJarFile));
	}

	@SuppressWarnings("HardcodedFileSeparator")
	@NotNull
	private static URL fileToJarUrl(@NotNull final File jarFile)
	{
		final URI uri = jarFile.toURI();
		try
		{
			return new URL("jar:" + uri.toString() + "!/");
		}
		catch (final MalformedURLException e)
		{
			throw new IllegalStateException("Should never happen", e);
		}
	}
}
