//
//  DashboardScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 11/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct DashboardScreen: View {
    @State private var selection: Int
    @State private var showTabBar = true
    var body: some View {
        
        TabView(selection: $selection) {
//
//            CategoriesScreen(selection: $selection, showTabBar: $showTabBar)
//                .tabItem {
//                    Image(systemName: "pencil")
//                    Text("Edit")
//                        .font(.caption)
//                    
//                }
//                .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, alignment: .topLeading)
//                .tag(0)
//           
//   
//            HomeScreen(showTabBar: $showTabBar)
//                .tabItem{
//                    Image(systemName: "house")
//                     Text("Home")
//                         .font(.caption)
//                }
//                .tag(1)
            
           
        }

        

    }
}


struct HideTabBarHelper: UIViewControllerRepresentable {
    var showTabBar : Bool
    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = UIViewController()
        DispatchQueue.main.async {
            viewController.tabBarController?.tabBar.isHidden = !showTabBar
        }
        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        DispatchQueue.main.async {
            uiViewController.tabBarController?.tabBar.isHidden = !showTabBar
        }
    }
}
