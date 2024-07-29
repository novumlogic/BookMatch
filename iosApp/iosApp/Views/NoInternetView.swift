//
//  NoInternetView.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 25/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct NoInternetView: View {
    var body: some View {
        VStack {
            Image(systemName: "wifi.slash")
                .resizable()
                .frame(width: 50, height: 50)
                .foregroundColor(Color.theme.primaryColor)
                .padding()

            Text("No Internet Connection")
                .font(.title)

            Text("Please check your internet connection and try again.")
                .font(.body)
                .multilineTextAlignment(.center)
                .padding(.horizontal,15)

            Button("Go to Settings") {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            }
            .padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(10)
            .padding(.vertical,30)
        }
        .background(Color.white)
        .cornerRadius(10)
        .shadow(radius: 10)
        .padding()
    }
}

#Preview {
    NoInternetView()
}
