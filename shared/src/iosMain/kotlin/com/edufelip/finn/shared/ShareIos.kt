package com.edufelip.finn.shared

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPopoverPresentationController
import platform.UIKit.UIViewController

internal fun presentShareSheet(text: String) {
    val activity = UIActivityViewController(activityItems = listOf(text), applicationActivities = null)
    // Find top-most presented view controller
    var top: UIViewController? = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (top?.presentedViewController != null) {
        top = top?.presentedViewController
    }
    // iPad popover anchor
    val pop: UIPopoverPresentationController? = activity.popoverPresentationController
    if (pop != null && top != null) {
        pop.sourceView = top.view
        top.view?.let { pop.sourceRect = it.bounds }
    }
    top?.presentViewController(activity, animated = true, completion = null)
}
