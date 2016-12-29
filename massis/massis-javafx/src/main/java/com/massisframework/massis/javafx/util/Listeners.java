package com.massisframework.massis.javafx.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;

public final class Listeners {

	public static <T> WeakChangeListener<T> weakL(ChangeListener<T> cl)
	{
		return new WeakChangeListener<T>(cl);
	}

	public static <T> WeakChangeListener<T> weakL(
			SimpleChangeListener<T> cl)
	{
		return new WeakChangeListener<T>((obs, o, n) -> cl.change(o, n));
	}

	public static <T> WeakChangeListener<T> weakL(
			NewValueChangeListener<T> cl)
	{
		return new WeakChangeListener<T>((obs, o, n) -> cl.change(n));
	}

	public static <T extends Event> WeakEventHandler<T> weakEH(
			EventHandler<T> eh)
	{
		return new WeakEventHandler<>(eh);
	}

	@FunctionalInterface
	public static interface SimpleChangeListener<T> {
		public void change(T oldValue, T newValue);
	}

	@FunctionalInterface
	public static interface NewValueChangeListener<T> {
		public void change(T newValue);
	}
}
