/*
 * This file is part of intellij-orogeny. It is subject to the licence terms in the COPYRIGHT file found in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT. No part of intellij-orogeny, including this file, may be copied, modified, propagated, or distributed except according to the terms contained in the COPYRIGHT file.
 * Copyright © 2015 The developers of intellij-orogeny. See the COPYRIGHT file in the top-level directory of this distribution and at https://raw.githubusercontent.com/KisanHub/developjs/master/COPYRIGHT.
 */

package com.kisanhub.intellij.orogeny.plugin.testing;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.*;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.PicoContainer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@SuppressWarnings({"ClassWithTooManyMethods", "OverlyComplexClass"})
public final class DelegatingApplicationEx implements ApplicationEx
{
	@SuppressWarnings({"RedundantFieldInitialization", "StaticNonFinalField"})
	@Nullable
	private static volatile DelegatingApplicationEx delegatingApplicationEx = null;

	@SuppressWarnings("FieldRepeatedlyAccessedInMethod")
	private static void enableUnitTestMode()
	{
		if (delegatingApplicationEx == null)
		{
			final ApplicationEx parent = ApplicationManagerEx.getApplicationEx();
			final DelegatingApplicationEx instance = new DelegatingApplicationEx(parent);
			//noinspection NonThreadSafeLazyInitialization
			delegatingApplicationEx = instance;
			ApplicationManager.setApplication(instance, parent);
		}
		else
		{
			//noinspection ConstantConditions
			delegatingApplicationEx.isUnitTestMode = true;
		}
	}

	@SuppressWarnings({"ConstantConditions", "FieldRepeatedlyAccessedInMethod"})
	private static void disableUnitTestMode()
	{
		if (delegatingApplicationEx == null)
		{
			throw new IllegalStateException("Call enableUnitTestMode first");
		}
		if (!delegatingApplicationEx.isUnitTestMode)
		{
			throw new IllegalStateException("isUnitTestMode should not be false");
		}
		delegatingApplicationEx.isUnitTestMode = false;
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	public static synchronized void runInUnitTestMode(@NotNull final Runnable runnable)
	{
		enableUnitTestMode();
		try
		{
			runnable.run();
		}
		finally
		{
			disableUnitTestMode();
		}
	}

	@NotNull
	private final ApplicationEx delegate;

	@SuppressWarnings("BooleanVariableAlwaysNegated")
	private boolean isUnitTestMode;

	public DelegatingApplicationEx(@NotNull final ApplicationEx applicationEx)
	{
		delegate = applicationEx;
		isUnitTestMode = true;
	}

	@Override
	public boolean isUnitTestMode()
	{
		return isUnitTestMode;
	}

	@Override
	public void load(final String optionsPath) throws IOException
	{
		delegate.load(optionsPath);
	}

	@Override
	public boolean isLoaded()
	{
		return delegate.isLoaded();
	}

	@NotNull
	@Override
	public String getName()
	{
		return delegate.getName();
	}

	@Override
	public boolean holdsReadLock()
	{
		return delegate.holdsReadLock();
	}

	@Override
	public boolean isWriteActionInProgress()
	{
		return delegate.isWriteActionInProgress();
	}

	@Override
	public boolean isWriteActionPending()
	{
		return delegate.isWriteActionPending();
	}

	@Override
	public void doNotSave()
	{
		delegate.doNotSave();
	}

	@Override
	public void doNotSave(final boolean value)
	{
		delegate.doNotSave(value);
	}

	@Override
	public boolean isDoNotSave()
	{
		return delegate.isDoNotSave();
	}

	@Override
	public void exit(final boolean force, final boolean exitConfirmed)
	{
		delegate.exit(force, exitConfirmed);
	}

	@Override
	public void restart(final boolean exitConfirmed)
	{
		delegate.restart(exitConfirmed);
	}

	@Override
	public boolean runProcessWithProgressSynchronously(@NotNull final Runnable process, @NotNull final String progressTitle, final boolean canBeCanceled, final Project project)
	{
		return delegate.runProcessWithProgressSynchronously(process, progressTitle, canBeCanceled, project);
	}

	@Override
	public boolean runProcessWithProgressSynchronously(@NotNull final Runnable process, @NotNull final String progressTitle, final boolean canBeCanceled, final Project project, final JComponent parentComponent)
	{
		return delegate.runProcessWithProgressSynchronously(process, progressTitle, canBeCanceled, project, parentComponent);
	}

	@Override
	public boolean runProcessWithProgressSynchronously(@NotNull final Runnable process, @NotNull final String progressTitle, final boolean canBeCanceled, final Project project, final JComponent parentComponent, final String cancelText)
	{
		return delegate.runProcessWithProgressSynchronously(process, progressTitle, canBeCanceled, project, parentComponent, cancelText);
	}

	@Override
	public void assertIsDispatchThread(final JComponent component)
	{
		delegate.assertIsDispatchThread(component);
	}

	@Override
	public void assertTimeConsuming()
	{
		delegate.assertTimeConsuming();
	}

	@Override
	public void runEdtSafeAction(@NotNull final Runnable runnable)
	{
		delegate.runEdtSafeAction(runnable);
	}

	@Override
	public boolean tryRunReadAction(@NotNull final Runnable action)
	{
		return delegate.tryRunReadAction(action);
	}

	@Override
	public void runReadAction(@NotNull final Runnable action)
	{
		delegate.runReadAction(action);
	}

	@Nullable
	@Override
	public <T> T runReadAction(@NotNull final Computable<T> computation)
	{
		return delegate.runReadAction(computation);
	}

	@Nullable
	@Override
	public <T, E extends Throwable> T runReadAction(@NotNull final ThrowableComputable<T, E> computation) throws E
	{
		return delegate.runReadAction(computation);
	}

	@Override
	public void runWriteAction(@NotNull final Runnable action)
	{
		delegate.runWriteAction(action);
	}

	@Nullable
	@Override
	public <T> T runWriteAction(@NotNull final Computable<T> computation)
	{
		return delegate.runWriteAction(computation);
	}

	@Nullable
	@Override
	public <T, E extends Throwable> T runWriteAction(@NotNull final ThrowableComputable<T, E> computation) throws E
	{
		return delegate.runWriteAction(computation);
	}

	@Override
	public boolean hasWriteAction(final Class<?> actionClass)
	{
		return delegate.hasWriteAction(actionClass);
	}

	@Override
	public void assertReadAccessAllowed()
	{
		delegate.assertReadAccessAllowed();
	}

	@Override
	public void assertWriteAccessAllowed()
	{
		delegate.assertWriteAccessAllowed();
	}

	@Override
	public void assertIsDispatchThread()
	{
		delegate.assertIsDispatchThread();
	}

	@Override
	public void addApplicationListener(@NotNull final ApplicationListener listener)
	{
		delegate.addApplicationListener(listener);
	}

	@Override
	public void addApplicationListener(@NotNull final ApplicationListener listener, @NotNull final Disposable parent)
	{
		delegate.addApplicationListener(listener, parent);
	}

	@Override
	public void removeApplicationListener(@NotNull final ApplicationListener listener)
	{
		delegate.removeApplicationListener(listener);
	}

	@Override
	public void saveAll()
	{
		delegate.saveAll();
	}

	@Override
	public void saveSettings()
	{
		delegate.saveSettings();
	}

	@Override
	public void exit()
	{
		delegate.exit();
	}

	@Override
	public boolean isWriteAccessAllowed()
	{
		return delegate.isWriteAccessAllowed();
	}

	@Override
	public boolean isReadAccessAllowed()
	{
		return delegate.isReadAccessAllowed();
	}

	@Override
	public boolean isDispatchThread()
	{
		return delegate.isDispatchThread();
	}

	@Override
	@NotNull
	public ModalityInvokator getInvokator()
	{
		return delegate.getInvokator();
	}

	@Override
	public void invokeLater(@NotNull final Runnable runnable)
	{
		delegate.invokeLater(runnable);
	}

	@Override
	public void invokeLater(@NotNull final Runnable runnable, @SuppressWarnings("rawtypes") @NotNull final Condition expired)
	{
		delegate.invokeLater(runnable, expired);
	}

	@Override
	public void invokeLater(@NotNull final Runnable runnable, @NotNull final ModalityState state)
	{
		delegate.invokeLater(runnable, state);
	}

	@Override
	public void invokeLater(@NotNull final Runnable runnable, @NotNull final ModalityState state, @SuppressWarnings("rawtypes") @NotNull final Condition expired)
	{
		delegate.invokeLater(runnable, state, expired);
	}

	@Override
	public void invokeAndWait(@NotNull final Runnable runnable, @NotNull final ModalityState modalityState)
	{
		delegate.invokeAndWait(runnable, modalityState);
	}

	@Override
	@NotNull
	public ModalityState getCurrentModalityState()
	{
		return delegate.getCurrentModalityState();
	}

	@Override
	@NotNull
	public ModalityState getModalityStateForComponent(@SuppressWarnings("StandardVariableNames") @NotNull final Component c)
	{
		return delegate.getModalityStateForComponent(c);
	}

	@Override
	@NotNull
	public ModalityState getDefaultModalityState()
	{
		return delegate.getDefaultModalityState();
	}

	@Override
	@NotNull
	public ModalityState getNoneModalityState()
	{
		return delegate.getNoneModalityState();
	}

	@Override
	@NotNull
	public ModalityState getAnyModalityState()
	{
		return delegate.getAnyModalityState();
	}

	@Override
	public long getStartTime()
	{
		return delegate.getStartTime();
	}

	@Override
	public long getIdleTime()
	{
		return delegate.getIdleTime();
	}

	@Override
	public boolean isHeadlessEnvironment()
	{
		return delegate.isHeadlessEnvironment();
	}

	@Override
	public boolean isCommandLine()
	{
		return delegate.isCommandLine();
	}

	@Override
	public boolean isDisposed()
	{
		return delegate.isDisposed();
	}

	@Override
	@NotNull
	public Future<?> executeOnPooledThread(@NotNull final Runnable action)
	{
		return delegate.executeOnPooledThread(action);
	}

	@Override
	@NotNull
	public <T> Future<T> executeOnPooledThread(@NotNull final Callable<T> action)
	{
		return delegate.executeOnPooledThread(action);
	}

	@Override
	public boolean isDisposeInProgress()
	{
		return delegate.isDisposeInProgress();
	}

	@Override
	public boolean isRestartCapable()
	{
		return delegate.isRestartCapable();
	}

	@Override
	public void restart()
	{
		delegate.restart();
	}

	@Override
	public boolean isActive()
	{
		return delegate.isActive();
	}

	@Override
	@NotNull
	public AccessToken acquireReadActionLock()
	{
		return delegate.acquireReadActionLock();
	}

	@Override
	@NotNull
	public AccessToken acquireWriteActionLock(@Nullable final Class marker)
	{
		return delegate.acquireWriteActionLock(marker);
	}

	@Override
	public boolean isInternal()
	{
		return delegate.isInternal();
	}

	@Override
	public boolean isEAP()
	{
		return delegate.isEAP();
	}

	@Nullable
	@Override
	public BaseComponent getComponent(@NotNull final String name)
	{
		return delegate.getComponent(name);
	}

	@Nullable
	@Override
	public <T> T getComponent(@NotNull final Class<T> interfaceClass)
	{
		return delegate.getComponent(interfaceClass);
	}

	@Nullable
	@Override
	public <T> T getComponent(@NotNull final Class<T> interfaceClass, final T defaultImplementationIfAbsent)
	{
		return delegate.getComponent(interfaceClass, defaultImplementationIfAbsent);
	}

	@Override
	public boolean hasComponent(@SuppressWarnings("rawtypes") @NotNull final Class interfaceClass)
	{
		return delegate.hasComponent(interfaceClass);
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	@NotNull
	public <T> T[] getComponents(@NotNull final Class<T> baseClass)
	{
		return delegate.getComponents(baseClass);
	}

	@Override
	@NotNull
	public PicoContainer getPicoContainer()
	{
		return delegate.getPicoContainer();
	}

	@Override
	@NotNull
	public MessageBus getMessageBus()
	{
		return delegate.getMessageBus();
	}

	@Override
	@NotNull
	public <T> T[] getExtensions(@NotNull final ExtensionPointName<T> extensionPointName)
	{
		return delegate.getExtensions(extensionPointName);
	}

	@SuppressWarnings("rawtypes")
	@Override
	@NotNull
	public Condition getDisposed()
	{
		return delegate.getDisposed();
	}

	@Override
	@Nullable
	public <T> T getUserData(@NotNull final Key<T> key)
	{
		return delegate.getUserData(key);
	}

	@Override
	public <T> void putUserData(@NotNull final Key<T> key, @Nullable final T value)
	{
		delegate.putUserData(key, value);
	}

	@Override
	public void dispose()
	{
		delegate.dispose();
	}
}
