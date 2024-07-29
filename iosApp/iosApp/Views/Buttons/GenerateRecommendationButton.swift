//
//  GenerateRecommendationButton.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 15/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct GenerateRecommendationButton : View {
    var body: some View {
        Button{
            
        } label: {
            ZStack(alignment: .center){
                RoundedRectangle(cornerRadius: /*@START_MENU_TOKEN@*/25.0/*@END_MENU_TOKEN@*/)
                    .fill(Color.theme.primaryContainer)
                    .frame(width: 80,height: 80)
                    .onTapGesture {
                        
                    }
                Image("GenerateLogo")
                    .resizable()
                    .frame(width: 30, height: 30)
            }
        
            
        }
        .padding(.bottom,40)
        

    }
}

#Preview {
    GenerateRecommendationButton()
}
