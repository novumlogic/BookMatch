//
//  OrientationControl.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit

class AppDelegate: NSObject, UIApplicationDelegate {
    static var orientationLock = UIInterfaceOrientationMask.portrait
}

func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
    return AppDelegate.orientationLock
}
