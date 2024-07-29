//
//  HomeScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 05/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared



struct HomeScreen: View {
   
    @EnvironmentObject var sharedViewModel: SharedViewModel
    @StateObject private var homeViewModel = HomeViewModel()
    @StateObject private var networkViewModel = NetworkViewModel()
    
    var body: some View {
        ZStack{
            switch sharedViewModel.tabSelection {
            case 0:
                CategoriesScreen {
                    
                    if sharedViewModel.selectedCategories.count >= 2 && sharedViewModel.selectedCategories.count <= 5{
                        sharedViewModel.tabSelection = 1
                        Task{
                            await sharedViewModel.setCategoryShown(value: true) 
                        }
                        UserDefaults.standard.setStringArray(sharedViewModel.selectedCategories, forKey: "selectedCategories")
                    } else {
                        sharedViewModel.showCategoryLimitAlert = true
                    }
                    
             
                }
                .blur(radius: networkViewModel.showAlert ? 3.0 : 0.0)
                .disabled(networkViewModel.showAlert)
                
            default :
                HomeContent(sharedViewModel: sharedViewModel, homeViewModel: homeViewModel, networkViewModel: networkViewModel)
                    .blur(radius: networkViewModel.showAlert ? 3.0 : 0.0)
                    .disabled(networkViewModel.showAlert)
            }
            
            if networkViewModel.showAlert {
                NoInternetView()
                    .transition(.opacity)

            }
        }
        .animation(.easeIn, value: networkViewModel.showAlert)
    }
        
}




#Preview {
    HomeScreen()
        .environmentObject(SharedViewModel())
}
