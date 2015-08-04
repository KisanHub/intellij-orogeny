/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/intellij-orogeny/master/COPYRIGHT.
 */

package com.kisanhub.intellij.useful.commandLine.external.support;

import org.jetbrains.annotations.NotNull;

public final class Argument
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <V> Argument argument(@NotNull final V value)
	{
		final Class<?> clazz = value.getClass();
		assert clazz != null;
		return new Argument(clazz, value);
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <V, K extends V> Argument argument(@NotNull final Class<K> parameterType, @NotNull final V value)
	{
		return new Argument(parameterType, value);
	}

	@NotNull
	private final Class<?> parameterType;

	@NotNull
	private final Object value;

	private Argument(@NotNull final Class<?> parameterType, @NotNull final Object value)
	{
		this.parameterType = parameterType;
		this.value = value;
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	@NotNull
	public static Class<?>[] toParameterTypes(@NotNull final Argument... arguments)
	{
		final int length = arguments.length;
		final Class<?>[] parameterTypes = new Class[length];
		for(int index = 0; index < length; index++)
		{
			parameterTypes[index] = arguments[index].parameterType;
		}
		return parameterTypes;
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	@NotNull
	public static Object[] toValues(@NotNull final Argument... arguments)
	{
		final int length = arguments.length;
		final Object[] values = new Object[length];
		for(int index = 0; index < length; index++)
		{
			values[index] = arguments[index].value;
		}
		return values;
	}
}
