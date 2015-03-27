/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.moduleOrderEntries;

import com.intellij.openapi.roots.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("AbstractClassWithOnlyOneDirectInheritor")
public abstract class NonNullPerOrderEntryRootPolicy<R> extends PerOrderEntryRootPolicy<R>
{
	@SuppressWarnings("AbstractMethodOverridesAbstractMethod")
	@NotNull
	@Override
	public abstract R visitModuleSourceOrderEntry(@NotNull final ModuleSourceOrderEntry moduleSourceOrderEntry, @SuppressWarnings("NullableProblems") @NotNull final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesAbstractMethod")
	@NotNull
	@Override
	public abstract R visitLibraryOrderEntry(@NotNull final LibraryOrderEntry libraryOrderEntry, @SuppressWarnings("NullableProblems") @NotNull final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesAbstractMethod")
	@NotNull
	@Override
	public abstract R visitModuleOrderEntry(@NotNull final ModuleOrderEntry moduleOrderEntry, @SuppressWarnings("NullableProblems") @NotNull final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesAbstractMethod")
	@NotNull
	@Override
	public abstract R visitModuleJdkOrderEntry(@NotNull final ModuleJdkOrderEntry jdkOrderEntry, @SuppressWarnings("NullableProblems") @NotNull final R initialValue);

	@SuppressWarnings("AbstractMethodOverridesAbstractMethod")
	@NotNull
	@Override
	public abstract R visitInheritedJdkOrderEntry(@NotNull final InheritedJdkOrderEntry inheritedJdkOrderEntry, @SuppressWarnings("NullableProblems") @NotNull final R initialValue);
}
