package org.masouras.boundary;

import java.io.File;

public interface PrintingDataEntityInitialPersister {
    Long initialPersist(File triggerFile);
}
