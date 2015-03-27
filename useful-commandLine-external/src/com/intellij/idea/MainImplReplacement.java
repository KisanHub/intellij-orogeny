/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.intellij.idea;

import com.intellij.idea.StartupUtil.AppStarter;
import com.kisanhub.intellij.useful.commandLine.external.support.RegisteringIdeaApplication;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ide.plugins.PluginManager.installExceptionHandler;
import static com.intellij.idea.StartupUtil.prepareAndStart;
import static com.intellij.util.PlatformUtils.*;
import static java.lang.System.setProperty;
import static javax.swing.SwingUtilities.invokeLater;

@SuppressWarnings({"UnusedDeclaration", "UtilityClass"})
public final class MainImplReplacement
{
	/**
	 * Called from PluginManager via reflection.
	 */
	@SuppressWarnings("AccessOfSystemProperties")
	public static void start(@NotNull final String... commandLineArguments)
	{
		if (commandLineArguments.length == 0)
		{
			throw new IllegalStateException("commandLineArguments must contain at least a command");
		}

		// Gets overwritten by StartupUtil and Logger's appalling code, which requires replacing prepareAndStart with some nasty reflection
		//final RecordingLoggerFactory recordingLoggerFactory = replaceLoggingFactory();

		final String platformPrefix = getPlatformPrefix(IDEA_CE_PREFIX);
		assert platformPrefix != null;
		setProperty(PLATFORM_PREFIX_KEY, platformPrefix);

		prepareAndStart(commandLineArguments, new AppStarter()
		{
			@Override
			public void start(final boolean newConfigFolder)
			{
				invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						installExceptionHandler();

						final IdeaApplication ideaApplication = new RegisteringIdeaApplication(commandLineArguments);

						invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								ideaApplication.run();
							}
						});
					}
				});
			}
		});
	}

	private MainImplReplacement()
	{
	}
}
