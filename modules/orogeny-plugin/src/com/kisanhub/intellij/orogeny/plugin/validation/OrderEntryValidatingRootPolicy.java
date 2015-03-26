/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.validation;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.kisanhub.intellij.orogeny.plugin.validation.projectValidationMessagesRecorders.ProjectValidationMessagesRecorder;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.compiler.CompilerMessageCategory.ERROR;
import static com.intellij.openapi.roots.OrderRootType.getAllTypes;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class OrderEntryValidatingRootPolicy extends RootPolicy<ProjectValidationMessagesRecorder>
{
	@NotNull
	public static final RootPolicy<ProjectValidationMessagesRecorder> OrderEntryValidatingRootPolicyInstance = new OrderEntryValidatingRootPolicy();

	private static final int OrderEntryLength = OrderEntry.class.getSimpleName().length();

	private OrderEntryValidatingRootPolicy()
	{
	}
	
	@SuppressWarnings("RefusedBequest")
	@Override
	public ProjectValidationMessagesRecorder visitModuleSourceOrderEntry(@NotNull final ModuleSourceOrderEntry moduleSourceOrderEntry, @NotNull final ProjectValidationMessagesRecorder value)
	{
		validate(value, moduleSourceOrderEntry, ModuleSourceOrderEntry.class);
		return value;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public ProjectValidationMessagesRecorder visitLibraryOrderEntry(@NotNull final LibraryOrderEntry libraryOrderEntry, @NotNull final ProjectValidationMessagesRecorder value)
	{
		validate(value, libraryOrderEntry, LibraryOrderEntry.class);
		return value;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public ProjectValidationMessagesRecorder visitModuleOrderEntry(@NotNull final ModuleOrderEntry moduleOrderEntry, @NotNull final ProjectValidationMessagesRecorder value)
	{
		validate(value, moduleOrderEntry, ModuleOrderEntry.class);
		return value;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public ProjectValidationMessagesRecorder visitModuleJdkOrderEntry(@NotNull final ModuleJdkOrderEntry jdkOrderEntry, @NotNull final ProjectValidationMessagesRecorder value)
	{
		validate(value, jdkOrderEntry, ModuleJdkOrderEntry.class);
		return value;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public ProjectValidationMessagesRecorder visitInheritedJdkOrderEntry(@NotNull final InheritedJdkOrderEntry inheritedJdkOrderEntry, @NotNull final ProjectValidationMessagesRecorder initialValue)
	{
		validate(initialValue, inheritedJdkOrderEntry, InheritedJdkOrderEntry.class);
		return initialValue;
	}
	
	private static <O extends OrderEntry> void validate(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder, @NotNull final O orderEntry, @NotNull final Class<O> orderEntryClass)
	{
		if (!orderEntry.isValid())
		{
			assert ERROR != null;
			projectValidationMessagesRecorder.record(project(orderEntry), ERROR, getOrderEntryDescription(orderEntry, orderEntryClass));
			return;
		}

		final OrderRootType[] allTypes = getAllTypes();
		assert allTypes != null;

		for (final OrderRootType orderRootType : allTypes)
		{
			validateRoots(projectValidationMessagesRecorder, orderEntry, orderEntryClass, orderRootType);
		}
	}

	private static <O extends OrderEntry> void validateRoots(@NotNull final ProjectValidationMessagesRecorder projectValidationMessagesRecorder, @NotNull final O orderEntry, @NotNull final Class<O> orderEntryClass, @NotNull final OrderRootType orderRootType)
	{
		for (final VirtualFile file : orderEntry.getFiles(orderRootType))
		{
			if (!file.isValid())
			{
				assert ERROR != null;
				final String format = format(ENGLISH, "Root '%1$s' of type '%2$s' in %3$s", file.getName(), orderRootType.name(), getOrderEntryDescription(orderEntry,orderEntryClass)); //NON-NLS
				assert format != null;
				projectValidationMessagesRecorder.record(project(orderEntry), ERROR, format);
			}
		}
	}

	@NotNull
	private static Project project(@NotNull final OrderEntry orderEntry)
	{
		return orderEntry.getOwnerModule().getProject();
	}

	@NotNull
	private static <O extends OrderEntry> String getOrderEntryDescription(@NotNull final O orderEntry, @NotNull final Class<O> orderEntryClass)
	{
		final Module ownerModule = orderEntry.getOwnerModule();
		final String simpleName = orderEntryClass.getSimpleName();
		final String format = format(ENGLISH, "%1$s '%2$s' in module '%3$s' is invalid", simpleName.substring(0, simpleName.length() - OrderEntryLength), orderEntry.getPresentableName(), ownerModule.getName()); //NON-NLS
		assert format != null;
		return format;
	}
}
