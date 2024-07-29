//
//  CustomTabBar.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 15/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct CustomTabBar: View {
    
    @EnvironmentObject var sharedViewModel: SharedViewModel

    let generateRecommendation: () -> Void
    var body: some View {
 
            HStack{
                Spacer()
                TabBarItem(icon: "pencil", title: "Edit", selectedIndex: sharedViewModel.tabSelection)
                    .onTapGesture {
                        sharedViewModel.tabSelection = 0
                        Task{
                            await sharedViewModel.setCategoryShown(value:false)
                        }
                    }
                    .foregroundStyle(sharedViewModel.tabSelection == 0 ? .blue : .black)
                Spacer()
                
                Button{
                    generateRecommendation()
                } label: {
                
                    ZStack(alignment: .center) {
                        Circle()
                            .fill(Color.theme.primaryContainer)
                            .frame(width: 80,
                                   height: 80)
                        Image("GenerateLogo")
                    }
                    .shadow(radius: 10)
                }
                .padding(.top,-50)
                
                Spacer()
                TabBarItem(icon: "house", title: "Home", selectedIndex: sharedViewModel.tabSelection)
                    .onTapGesture {
                        sharedViewModel.tabSelection = 1
                    }
                    .foregroundStyle(sharedViewModel.tabSelection == 1 ? .blue : .black)
                Spacer()
            }
            .padding(.top,10)
        }
        
    
}


struct TabBarItem: View{
    
    let icon: String
    let title: String
    let selectedIndex: Int
    var body: some View{
        VStack{
            Image(systemName: icon)
                .imageScale(.large)
                .font(.headline)
            Text(title)
                .font(.footnote)
        }
    }
}
#Preview {
    CustomTabBar(generateRecommendation: {
        
    })
}
