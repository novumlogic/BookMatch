//
//  Color.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 08/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

extension Color{
    static let theme = BookMatchTheme.ColorTheme()
}

extension Font{
    static let type = BookMatchTheme.Typography()
}

struct BookMatchTheme {
    struct Typography {
        let displayMedium  = Font.custom("roboto_regular", size: 45 )
//        let titleMedium = Font.custom("roboto_medium", size: 16 )
        let headlineLarge = Font.custom("roboto_regular", size: 32)
        let labelLarge = Font.custom("roboto_medium", size: 14)
//        let bodyMedium =
//        let bodySmall =
    }
    struct ColorTheme {
     
        let primaryColor = Color("PrimaryColor")
        let onPrimary = Color("OnPrimary")
        let onPrimaryContainer = Color("OnPrimaryContainer")
        let onSecondaryContainer = Color("OnSecondaryContainer")
        let onSecondaryContainer_8o = Color("OnSecondaryContainer8o")
        let onSurfaceVariant = Color("OnSurfaceVariant")
        let outline = Color("Outline")
        let outlineVariant = Color("OutlineVariant")
        let primary95 = Color("Primary95")
        let primaryContainer = Color("PrimaryContainer")
        let secondaryContainer = Color("SecondaryContainer")
        let secondaryFixedDim = Color("SecondaryFixedDim")
        let surface = Color("Surface")
        let lightOrange = Color("LightOrange")
    }
}


