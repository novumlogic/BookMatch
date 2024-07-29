//
//  RatingBar.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct RatingBar: View{
    let rating: Int?
    //pass the rating upwards
    let onRatingChange: (Int) -> Void
    
    var body: some View {
        HStack {
            Text("Your rating: ")
                .font(.callout)
    
            ForEach(1..<6) { index in
                    
                Image(systemName: "star.fill")
                    .foregroundColor(index <= rating ?? 0 ? Color.theme.primaryColor : .gray)
                    .font(.callout)
                    .onTapGesture {
                        onRatingChange(index)
                    }
            }
        }
        .font(.largeTitle)
        .padding()
    }
}

#Preview {
    RatingBar(rating: 2) { _ in
        
    }
}
