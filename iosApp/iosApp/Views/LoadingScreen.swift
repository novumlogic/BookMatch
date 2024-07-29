//
//  LoadingScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct LoadingScreen: View {
    @State private var isAnimating = false
    @Binding var recommendationStatus: RecommendationStatus
    
    var body: some View {
        ZStack{
            Color(.black)
                .opacity(0.8)
                .ignoresSafeArea()
            
            VStack(spacing: 10) {
           
                Image("AppLogo")
                    .resizable()
                    .frame(width: 200, height: 200)
                    .scaleEffect(isAnimating ? 1.2 : 1.0)
                    .animation(Animation.easeInOut(duration: 0.6).repeatForever(autoreverses: true), value: isAnimating)
                
                Text("Keep calm, loading your books ")
                    .foregroundStyle(.white)
                    .font(.title3)
                    .bold()
            }
            .onAppear {
                isAnimating = true
            }
        }
        .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, maxHeight: .infinity)
    }
}

#Preview {
    LoadingScreen(recommendationStatus: .constant(RecommendationStatus.loading))
}
