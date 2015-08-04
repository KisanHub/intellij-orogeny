/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */
package com.kisanhub.intellij.useful.commandLine.commandLineApplicationStarterExs;

import com.intellij.openapi.application.ApplicationStarterEx;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.idea.IdeaApplication.IDEA_IS_UNIT_TEST;
import static java.lang.Boolean.TRUE;
import static java.lang.System.*;

@SuppressWarnings("AbstractClassWithOnlyOneDirectInheritor")
public abstract class AbstractCommandLineApplicationStarterEx extends ApplicationStarterEx
{
	public static final int SuccessExitCode = 0;
	public static final int ErrorExitCode = 1;
	public static final int UnexpectedErrorExitCode = 2;

	@SuppressWarnings({"AccessOfSystemProperties", "ConstantConditions"})
	protected AbstractCommandLineApplicationStarterEx(final boolean forceUnitTestMode)
	{
		if (forceUnitTestMode)
		{
			// Causes ApplicationManager.getInstance().isUnitTestMode() to be true
			setProperty(IDEA_IS_UNIT_TEST, TRUE.toString());
		}
	}

	@Override
	@NotNull
	public final String toString()
	{
		return getCommandName();
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public final boolean canProcessExternalCommandLine()
	{
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public final void processExternalCommandLine(@NotNull final String[] args)
	{
		super.processExternalCommandLine(args);
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public final void processExternalCommandLine(@NotNull final String[] args, @Nullable final String currentDirectory)
	{
		main(args);
	}

	@Override
	public final boolean isHeadless()
	{
		return true;
	}

	@Override
	@NotNull
	@NonNls
	public final String getCommandName()
	{
		final Class<? extends AbstractCommandLineApplicationStarterEx> aClass = getClass();
		assert aClass != null;

		@Nullable final String canonicalName = aClass.getCanonicalName();
		if (canonicalName == null)
		{
			throw new IllegalStateException("An ApplicationStarter can not be a local or anonymous class");
		}
		return canonicalName;
	}

	@Override
	public final void premain(@NotNull final String... args)
	{
		// Process arguments here, exit(1) if invalid
	}

	@SuppressWarnings({"ConfusingMainMethod", "CallToSystemExit", "UseOfSystemOutOrSystemErr"})
	@Override
	public final void main(@NotNull final String... args)
	{
		final String ourCommandName = args[0];
		//noinspection CallToSimpleGetterFromWithinClass
		assert getCommandName().equals(ourCommandName);

		final int reducedBy = 1;
		final int reducedLength = args.length - reducedBy;
		final String[] commandLineArgumentsExcludingCommandName = new String[reducedLength];
		arraycopy(args, reducedBy, commandLineArgumentsExcludingCommandName, 0, reducedLength);

		final int exitCode;
		try
		{
			exitCode = execute(commandLineArgumentsExcludingCommandName);
		}
		catch (final Throwable e)
		{
			assert err != null;
			e.printStackTrace(err);
			exit(UnexpectedErrorExitCode);
			return;
		}
		exit(exitCode);
	}

	protected abstract int execute(@NotNull final String... commandLineArgumentsExcludingCommandName);
}
