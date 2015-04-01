/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.kisanhub.intellij.orogeny.plugin.testing.DelegatingApplicationEx.runInUnitTestMode;
import static sun.misc.Unsafe.getUnsafe;

public final class DelegateProgramRunner<S extends RunnerSettings> extends GenericProgramRunner<S>
{
	@NotNull
	private final GenericProgramRunner<S> delegate;

	@NotNull
	private final Method doExecute;

	public DelegateProgramRunner(@NotNull final GenericProgramRunner<S> delegate)
	{
		this.delegate = delegate;
		doExecute = getDoExecute();
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	protected void execute(@NotNull final ExecutionEnvironment environment, @Nullable final Callback callback, @NotNull final RunProfileState state) throws ExecutionException
	{
		final RunContentDescriptor runContentDescriptor = doExecute(state, environment);
		assert runContentDescriptor != null;

		@Nullable final ProcessHandler processHandler = runContentDescriptor.getProcessHandler();
		if (processHandler == null)
		{
			throw new ExecutionException("Null ProcessHandler");
		}

		runContentDescriptor.setExecutionId(environment.getExecutionId());

		// By re-ordering this before startNotify, we can add process handlers...
		if (callback != null)
		{
			callback.processStarted(runContentDescriptor);
		}

		// Fixes a bug with JUnit's TestPackage.isSyncSearch(), which is used when finding tests to run and relies on isUnitTestMode()
		// The only other option is to remove the ProcessListener (ProcessAdapter) is creates in TestPackage.createHandler(Executor executor)
		runInUnitTestMode(new Runnable()
		{
			@Override
			public void run()
			{
				processHandler.startNotify();
			}
		});
	}

	@SuppressWarnings({"RefusedBequest", "ThrowInsideCatchBlockWhichIgnoresCaughtException"})
	@Nullable
	@Override
	protected RunContentDescriptor doExecute(@NotNull final RunProfileState state, @NotNull final ExecutionEnvironment environment) throws ExecutionException
	{
		@Nullable final RunContentDescriptor runContentDescriptor;
		try
		{
			runContentDescriptor = (RunContentDescriptor) doExecute.invoke(delegate, state, environment);
		}
		catch (final IllegalAccessException e)
		{
			throw new IllegalStateException(e);
		}
		catch (final InvocationTargetException e)
		{
			final Throwable cause = e.getCause();
			if (cause instanceof ExecutionException)
			{
				throw (ExecutionException) cause;
			}
			@SuppressWarnings("UseOfSunClasses") final Unsafe unsafe = getUnsafe();
			assert unsafe != null;
			unsafe.throwException(cause);
			throw new IllegalStateException();
		}

		if (runContentDescriptor == null)
		{
			throw new ExecutionException("null RunContentDescriptor");
		}
		return runContentDescriptor;
	}

	@NotNull
	private Method getDoExecute()
	{
		@SuppressWarnings("rawtypes") final Class<? extends GenericProgramRunner> delegateClass = delegate.getClass();
		assert delegateClass != null;
		@SuppressWarnings("LocalVariableHidesMemberVariable") final Method doExecute;
		try
		{
			doExecute = delegateClass.getDeclaredMethod("doExecute", RunProfileState.class, ExecutionEnvironment.class);
		}
		catch (final NoSuchMethodException e)
		{
			throw new IllegalStateException(e);
		}
		assert doExecute != null;
		doExecute.setAccessible(true);
		return doExecute;
	}

	@Deprecated
	@SuppressWarnings({"deprecation", "RefusedBequest"})
	@Nullable
	@Override
	protected RunContentDescriptor doExecute(@NotNull final Project project, @NotNull final RunProfileState state, @Nullable final RunContentDescriptor contentToReuse, @NotNull final ExecutionEnvironment environment) throws ExecutionException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void execute(@NotNull final ExecutionEnvironment environment) throws ExecutionException
	{
		super.execute(environment);
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void execute(@NotNull final ExecutionEnvironment environment, @Nullable final Callback callback) throws ExecutionException
	{
		super.execute(environment, callback);
	}

	@NotNull
	@Override
	public String getRunnerId()
	{
		return delegate.getRunnerId();
	}

	@Override
	public boolean canRun(@NotNull final String executorId, @NotNull final RunProfile profile)
	{
		return delegate.canRun(executorId, profile);
	}

	@SuppressWarnings("RefusedBequest")
	@Nullable
	@Override
	public S createConfigurationData(final ConfigurationInfoProvider settingsProvider)
	{
		return delegate.createConfigurationData(settingsProvider);
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void checkConfiguration(final RunnerSettings settings, final ConfigurationPerRunnerSettings configurationPerRunnerSettings) throws RuntimeConfigurationException
	{
		delegate.checkConfiguration(settings, configurationPerRunnerSettings);
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void onProcessStarted(final RunnerSettings settings, final ExecutionResult executionResult)
	{
		delegate.onProcessStarted(settings, executionResult);
	}

	@SuppressWarnings("RefusedBequest")
	@Nullable
	@Override
	public SettingsEditor<S> getSettingsEditor(final Executor executor, final RunConfiguration configuration)
	{
		return delegate.getSettingsEditor(executor, configuration);
	}
}
