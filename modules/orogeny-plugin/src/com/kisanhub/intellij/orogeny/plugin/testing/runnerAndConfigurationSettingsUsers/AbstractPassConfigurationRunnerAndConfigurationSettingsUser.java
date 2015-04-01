/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing.runnerAndConfigurationSettingsUsers;

import com.intellij.execution.RunnerAndConfigurationSettings;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPassConfigurationRunnerAndConfigurationSettingsUser<D> implements RunnerAndConfigurationSettingsUser<D>
{
	@NotNull
	private final RunnerAndConfigurationSettingsUser<D> passConfigurationTo;

	protected AbstractPassConfigurationRunnerAndConfigurationSettingsUser(@NotNull final RunnerAndConfigurationSettingsUser<D> passConfigurationTo)
	{
		this.passConfigurationTo = passConfigurationTo;
	}

	@Override
	public final void use(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final D data)
	{
		if (shouldNotPassOnConfiguration(runnerAndConfigurationSettings, data))
		{
			return;
		}
		passConfigurationTo.use(runnerAndConfigurationSettings, data);
	}

	protected abstract boolean shouldNotPassOnConfiguration(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final D data);
}
