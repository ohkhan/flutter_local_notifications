#import <Foundation/Foundation.h>

NSBundle* flutter_local_notifications_SWIFTPM_MODULE_BUNDLE() {
    NSURL *bundleURL = [[[NSBundle mainBundle] bundleURL] URLByAppendingPathComponent:@"flutter_local_notifications_flutter_local_notifications.bundle"];

    NSBundle *preferredBundle = [NSBundle bundleWithURL:bundleURL];
    if (preferredBundle == nil) {
      return [NSBundle bundleWithPath:@"/Users/omarkhan/flutter_local_notifications/flutter_local_notifications/ios/flutter_local_notifications/.build/arm64-apple-macosx/debug/flutter_local_notifications_flutter_local_notifications.bundle"];
    }

    return preferredBundle;
}