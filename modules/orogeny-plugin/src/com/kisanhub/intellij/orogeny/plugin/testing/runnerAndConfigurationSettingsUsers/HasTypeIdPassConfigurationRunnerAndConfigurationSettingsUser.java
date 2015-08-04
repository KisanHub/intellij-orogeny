/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing.runnerAndConfigurationSettingsUsers;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class HasTypeIdPassConfigurationRunnerAndConfigurationSettingsUser<D> extends AbstractPassConfigurationRunnerAndConfigurationSettingsUser<D>
{
	@NotNull
	@NonNls
	public static final String JUnitTypeId = "JUnit";

	@NotNull
	private final String typeId;

	public HasTypeIdPassConfigurationRunnerAndConfigurationSettingsUser(@NotNull @NonNls final String typeId, @NotNull final RunnerAndConfigurationSettingsUser<D> passConfigurationTo)
	{
		super(passConfigurationTo);
		this.typeId = typeId;
	}

	@Override
	protected boolean shouldNotPassOnConfiguration(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings, @NotNull final D data)
	{
		final ConfigurationType type = runnerAndConfigurationSettings.getType();
		assert type != null;
		return !typeId.equals(type.getId());
	}
}
