package org.senkbeil.debugger.api.lowlevel.threads

import org.senkbeil.debugger.api.utils.PendingActionManager

/**
 * Provides pending thread death capabilities to an existing thread death
 * manager.
 *
 * Contains an internal pending action manager.
 */
trait StandardPendingThreadDeathSupport extends PendingThreadDeathSupport {
  override protected val pendingActionManager: PendingActionManager[ThreadDeathRequestInfo] =
    new PendingActionManager[ThreadDeathRequestInfo]
}