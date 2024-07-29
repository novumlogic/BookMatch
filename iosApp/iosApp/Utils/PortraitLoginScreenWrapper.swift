//
//  PortraitLoginScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct PortraitLoginScreenWrapper: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> some UIViewController {
        return PortraitHostingController(rootView: LoginScreen())
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
