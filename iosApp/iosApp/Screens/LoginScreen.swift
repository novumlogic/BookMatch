//
//  LoginScreen.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 05/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import AuthenticationServices
import Supabase
import GoogleSignIn
import GoogleSignInSwift

struct LoginScreen: View {
    
    @EnvironmentObject var sharedViewModel: SharedViewModel

    var body: some View {
        VStack{
            Spacer()
            VStack {
                Image("AppLogo")
                    .resizable()
                    .frame(width: 250,height: 250)
                
                Text("Login To\nGet Started")
                    .font(Font.type.headlineLarge)
                    .multilineTextAlignment(.center)
            }
            
            Spacer()

            Button{
                sharedViewModel.signIn()
            } label: {
                HStack {
                    Image("GoogleLogo")
                        .resizable()
                        .frame(width: 18, height: 18)
                    Text("Login with Google")
                        .foregroundStyle(Color.theme.primaryColor)
                        .font(Font.type.labelLarge)
                }
                .frame(width: 280, height: 50)
                .overlay(
                    RoundedRectangle(cornerRadius: 30.0)
                        .stroke(Color.theme.primaryColor,lineWidth: 1.5)
                )
            }
            .frame(height: 50)
            .padding(.bottom, 120)
            .padding(.horizontal, 40)
        }
        .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, maxHeight: .infinity)

    }
 

}

#Preview {
    LoginScreen()
}
