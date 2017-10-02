package com.ppolivka.plugin.gitcleaner;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.util.containers.Convertor;
import org.jetbrains.annotations.NotNull;

public class WorkerUtil {

    public static <T> T computeValueInModal(@NotNull Project project,
                                            @NotNull String caption,
                                            @NotNull final Convertor<ProgressIndicator, T> task) {
        return computeValueInModal(project, caption, true, task);
    }

    public static <T> T computeValueInModal(@NotNull Project project,
                                            @NotNull String caption,
                                            boolean canBeCancelled,
                                            @NotNull final Convertor<ProgressIndicator, T> task) {
        final Ref<T> dataRef = new Ref<>();
        final Ref<Throwable> exceptionRef = new Ref<>();
        ProgressManager.getInstance().run(new Task.Modal(project, caption, canBeCancelled) {
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    dataRef.set(task.convert(indicator));
                } catch (Throwable e) {
                    exceptionRef.set(e);
                }
            }
        });
        if (!exceptionRef.isNull()) {
            Throwable e = exceptionRef.get();
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            if (e instanceof Error) {
                throw ((Error) e);
            }
            throw new RuntimeException(e);
        }
        return dataRef.get();
    }

}
