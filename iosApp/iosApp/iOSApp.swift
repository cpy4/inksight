import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinInitKt.doInitKoin(platformConfig: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
