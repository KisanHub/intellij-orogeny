/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.RootPolicy;
import com.kisanhub.intellij.useful.moduleOrderEntries.ProcessModuleOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public final class UsefulModule implements ProcessModuleOrder
{
	@SuppressWarnings("PublicField")
	@NotNull
	public final Module module;

	@SuppressWarnings("PublicField")
	@NotNull
	public final ModuleRootManager moduleRootManager;

	// module may be ModuleEx
	public UsefulModule(@NotNull final Module module)
	{
		this.module = module;
		moduleRootManager = moduleRootManager();
	}

	@Override
	public <R> void useModuleOrderEntriesInModuleDependencyOrder(@NotNull final RootPolicy<R> rootPolicy, @Nullable final R initialValue)
	{
		moduleRootManager.processOrder(rootPolicy, initialValue);
	}

	@NotNull
	private ModuleRootManager moduleRootManager()
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		assert moduleRootManager != null;
		return moduleRootManager;
	}
}
