/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine.commandLineApplicationStarterExs;

import com.intellij.openapi.project.ex.ProjectEx;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.util.InvalidDataException;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.util.Locale.ENGLISH;

@SuppressWarnings("AbstractClassWithOnlyOneDirectInheritor")
public abstract class AbstractProjectUsingCommandLineApplicationStarterEx extends AbstractCommandLineApplicationStarterEx
{
	@NotNull
	private final UsingExecutor<ProjectEx> projectUsingExecutor;

	protected AbstractProjectUsingCommandLineApplicationStarterEx(final boolean forceUnitTestMode, @NotNull final UsingExecutor<ProjectEx> projectUsingExecutor)
	{
		super(forceUnitTestMode);
		this.projectUsingExecutor = projectUsingExecutor;
	}

	@Override
	protected final int execute(@NotNull final String... commandLineArgumentsExcludingCommandName)
	{
		final String projectFilePathString = commandLineArgumentsExcludingCommandName[0];
		assert projectFilePathString != null;
		return useProject(projectFilePathString);
	}

	public int useProject(@NotNull final String projectFilePathString)
	{
		final ProjectManagerEx projectManagerEx = ProjectManagerEx.getInstanceEx();
		projectManagerEx.blockReloadingProjectOnExternalChanges();

		final ProjectEx project;
		try
		{
			project = (ProjectEx) projectManagerEx.loadAndOpenProject(projectFilePathString);
			assert project != null;
		}
		catch (final IOException ignored)
		{
			final String format = format(ENGLISH, "Could not load project at path '%1$s'", projectFilePathString); //NON-NLS
			assert format !=null;
			return printMessageToStandardError(format);
		}
		catch (final JDOMException e)
		{
			final String format = format(ENGLISH, "Could not parse project XML from path '%1$s' ('%2$s')", projectFilePathString, e.getMessage()); //NON-NLS
			assert format !=null;
			return printMessageToStandardError(format);
		}
		catch (final InvalidDataException e)
		{
			final String format = format(ENGLISH, "Could not validate project data from path '%1$s' ('%2$s')", projectFilePathString, e.getMessage()); //NON-NLS
			assert format !=null;
			return printMessageToStandardError(format);
		}
		try
		{
			projectUsingExecutor.use(project);
		}
		finally
		{
			projectManagerEx.closeProject(project);
		}
		return SuccessExitCode;
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static int printMessageToStandardError(@NotNull final String format)
	{
		assert err != null;
		err.println(format);
		return ErrorExitCode;
	}
}
