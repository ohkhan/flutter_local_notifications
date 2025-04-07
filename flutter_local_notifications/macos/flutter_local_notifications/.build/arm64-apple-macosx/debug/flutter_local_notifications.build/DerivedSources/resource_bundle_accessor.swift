import Foundation

extension Foundation.Bundle {
    static let module: Bundle = {
        let mainPath = Bundle.main.bundleURL.appendingPathComponent("flutter_local_notifications_flutter_local_notifications.bundle").path
        let buildPath = "/Users/omarkhan/flutter_local_notifications/flutter_local_notifications/macos/flutter_local_notifications/.build/arm64-apple-macosx/debug/flutter_local_notifications_flutter_local_notifications.bundle"

        let preferredBundle = Bundle(path: mainPath)

        guard let bundle = preferredBundle ?? Bundle(path: buildPath) else {
            // Users can write a function called fatalError themselves, we should be resilient against that.
            Swift.fatalError("could not load resource bundle: from \(mainPath) or \(buildPath)")
        }

        return bundle
    }()
}