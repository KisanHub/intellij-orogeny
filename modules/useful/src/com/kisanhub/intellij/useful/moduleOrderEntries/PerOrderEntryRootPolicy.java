/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.moduleOrderEntries;

import com.intellij.openapi.roots.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"AbstractClassExtendsConcreteClass", "AbstractClassWithOnlyOneDirectInheritor"})
public abstract class PerOrderEntryRootPolicy<R> extends RootPolicy<R>
{
	protected PerOrderEntryRootPolicy()
	{
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@Nullable
	public final R visitOrderEntry(@NotNull final OrderEntry orderEntry, @Nullable final R value)
	{
		throw new UnsupportedOperationException("visitOrderEntry should not be visited");
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@Nullable
	public final R visitJdkOrderEntry(@NotNull final JdkOrderEntry jdkOrderEntry, @Nullable final R value)
	{
		throw new UnsupportedOperationException("visitJdkOrderEntry should not be visited");
	}

	@SuppressWarnings("AbstractMethodOverridesConcreteMethod")
	@Override
	@Nullable
	public abstract R visitModuleSourceOrderEntry(@NotNull final ModuleSourceOrderEntry moduleSourceOrderEntry, @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @Nullable final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesConcreteMethod")
	@Override
	@Nullable
	public abstract R visitLibraryOrderEntry(@NotNull final LibraryOrderEntry libraryOrderEntry, @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @Nullable final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesConcreteMethod")
	@Override
	@Nullable
	public abstract R visitModuleOrderEntry(@NotNull final ModuleOrderEntry moduleOrderEntry, @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @Nullable final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesConcreteMethod")
	@Override
	@Nullable
	public abstract R visitModuleJdkOrderEntry(@NotNull final ModuleJdkOrderEntry jdkOrderEntry, @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @Nullable final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesConcreteMethod")
	@Override
	@Nullable
	public abstract R visitInheritedJdkOrderEntry(@NotNull final InheritedJdkOrderEntry inheritedJdkOrderEntry, @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") @Nullable final R initialValue);

}
