//
//  NetworkViewModel.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 24/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Combine

class NetworkViewModel: ObservableObject {
    @Published var showAlert = false
    private var networkMonitor = NetworkMonitor()
    private var cancellables = Set<AnyCancellable>()

    init() {
        networkMonitor.$isConnected
            .sink { [weak self] isConnected in
                if !isConnected {
                    self?.showAlert = true
                } else {
                    self?.showAlert = false
                }
            }
            .store(in: &cancellables)
    }
}
