//
//  NetworkMonitor.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 24/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Network
import SwiftUI
import Combine

class NetworkMonitor: ObservableObject {
    private var monitor: NWPathMonitor
    private var queue: DispatchQueue
    @Published var isConnected: Bool = true

    init() {
        monitor = NWPathMonitor()
        queue = DispatchQueue.global(qos: .background)
        monitor.pathUpdateHandler = { [weak self] path in
            DispatchQueue.main.async {
                self?.isConnected = path.status == .satisfied
            }
        }
        monitor.start(queue: queue)
    }

    deinit {
        monitor.cancel()
    }
}
