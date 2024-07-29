//
//  PortraitCategoriesScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct PortraitCategoriesScreenWrapper: UIViewControllerRepresentable {
    let onContinueClicked: () -> Void
    @EnvironmentObject var sharedViewModel: SharedViewModel

    func makeUIViewController(context: Context) -> some UIViewController {
        let categoriesScreen = CategoriesScreen(onContinueClicked: onContinueClicked)
            .environmentObject(sharedViewModel)
        return PortraitHostingController(rootView: categoriesScreen)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}
