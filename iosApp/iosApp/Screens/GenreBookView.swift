//
//  HomeSection.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 11/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct GenreBookView<Content: View>: View {
    let title: String
    let content: Content
    
    init(title: String, @ViewBuilder content: () -> Content) {
        self.title = title
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading){
            let modifiedTitle = formatString(input: title)
            Text(modifiedTitle)
                .font(.largeTitle)
                .padding([.leading, .top], 20)
            content
        }
    }
    
    func formatString(input: String) -> String {
        // Make the first character uppercase
        let formattedStart = input.first.map { "\($0)".uppercased() } ?? ""
        
        // Get the rest of the string without the first character
        let remainingString = String(input.dropFirst())
        
        // Prepend the formatted start to the remaining string
        let result = formattedStart + remainingString
        
        // Replace underscores with spaces
        let finalResult = result.replacingOccurrences(of: "_", with: " ")
        
        return finalResult
    }
}
