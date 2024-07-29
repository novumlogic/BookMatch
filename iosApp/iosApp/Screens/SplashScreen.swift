//
//  SplashScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 05/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct SplashScreen: View {
    @Binding var isActive: Bool
    let onSplashShown : () -> Void
    var body: some View {
        VStack{
            Image("AppLogo" )
                .resizable()
                .frame(width: 250, height: 250)
                .padding([.bottom], -20)
            
            Text("Book Match")
                .font(Font.type.displayMedium)
                .foregroundColor(Color.theme.primaryColor)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.theme.lightOrange).edgesIgnoringSafeArea(/*@START_MENU_TOKEN@*/.all/*@END_MENU_TOKEN@*/)
        .onAppear(perform: {
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 2, execute: { 
                onSplashShown()
                self.isActive = true
            })
        })

        
    }
}

#Preview {
    SplashScreen(isActive: .constant(false)) {
        
    }
}
