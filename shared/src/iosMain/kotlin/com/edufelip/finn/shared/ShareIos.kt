package com.edufelip.finn.shared

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.valueForKey
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPopoverPresentationController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.bounds
import platform.UIKit.popoverPresentationController

@OptIn(ExperimentalForeignApi::class)
internal fun presentShareSheet(text: String) {
    val activity = UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
    val application = UIApplication.sharedApplication()
    val window = firstWindow(application)
    var controller: UIViewController? = window?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    val pop: UIPopoverPresentationController? = activity.popoverPresentationController
    if (pop != null && controller?.view != null) {
        pop.sourceView = controller.view
        controller.view?.let { pop.sourceRect = it.bounds }
    }
    controller?.presentViewController(activity, animated = true, completion = null)
}

private fun firstWindow(app: UIApplication): UIWindow? {
    (app.valueForKey("keyWindow") as? UIWindow)?.let { return it }
    val windows = app.valueForKey("windows") as? List<*>
    return windows?.firstOrNull { (it as? UIWindow)?.rootViewController != null } as? UIWindow
}
