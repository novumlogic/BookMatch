//
//  ImageHolder.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 23/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Kingfisher
import Shared

struct ImageHolder: View {
    
    @State private var imageSize: CGSize = .zero // Track image size
    @State private var showPlaceholder: Bool = false // Track if placeholder should be shown
    @State private var showProgressView: Bool = true // Track if progress view should be shown
    let urlString: String
    
    let frameWidth: CGFloat?
    let frameHeight: CGFloat?
    let topPadding: CGFloat?
    let cornerRadius: CGFloat?
    let maxWidth: CGFloat?
    let maxHeight: CGFloat?
    
    var body: some View {
        VStack {
            if showPlaceholder {
                Image("NoImage") // Placeholder image
                    .resizable()
                    .frame(width: 100, height: 100)
                    .frame(maxWidth: maxWidth, maxHeight: maxHeight)
                
            } else {
                KFImage( URL(string: urlString)!)
                    .onSuccess { result in
                        // Get the image size from the result
                        imageSize = result.image.size
                            // Determine if the image is too small
                            if imageSize.width < 5 || imageSize.height < 5 {
                                showPlaceholder = true
                            }
                        showProgressView = false
                    }
                    .onFailure { error in
                        print("Failed to load image: \(error)")
                        showPlaceholder = true // Show placeholder if image fails to load
                        showProgressView = false
                    }
                    
                    .resizable()
                    .frame(maxWidth: maxWidth, maxHeight: maxHeight)
                    .frame(width: frameWidth, height: frameHeight)
                    .padding(.top, topPadding)
                    .cornerRadius(cornerRadius ?? 0)
                    .overlay(
                        Group {
                            if showProgressView {
                                ProgressView() // Show progress view while loading
                            }
                        }
                    )
            }
        }
        .onAppear {
            // Reset placeholder flag when view appears
            showPlaceholder = false
        }
    }
}

#Preview {
    ImageHolder(urlString: "\(Constants.shared.OPEN_LIBRARY_API_URL)/9780307588364-L.jpg", frameWidth: nil, frameHeight: nil, topPadding: nil, cornerRadius: nil, maxWidth: nil, maxHeight: nil)
}
