//
//  HorizontalBookItem.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 26/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct HorizontalBookItem: View{
    let book: BookDetails
    var body: some View{
        HStack{
            VStack{
                Spacer()
                Text(book.bookName)
                    .padding(.leading,10)
                .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/,alignment: .leading)
                .multilineTextAlignment(.leading)
                .lineLimit(2, reservesSpace: true)
                
                Text(book.authorName)
                    .padding(.leading,10)
                .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/,alignment: .leading)
                Spacer()
            }
            .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/,alignment: .leading)
            
            ImageHolder(urlString: "\(Constants.shared.OPEN_LIBRARY_API_URL)/\(book.isbn)-L.jpg", frameWidth: 100, frameHeight: 100, topPadding: 0, cornerRadius: 12, maxWidth: nil, maxHeight: nil)
               
        }
        .frame(width: 300)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.theme.outlineVariant, lineWidth:2))
        .padding(.vertical,4)
        .padding(.horizontal,2)
    }
}

#Preview {
    HorizontalBookItem(book:BookDetails(
        bookName: "Horror Book 2",
        authorName: "Author E",
        genreTags: ["Horror", "Paranormal"],
        description: "Description of Horror Book 2",
        pages: "320",
        isbn: "9780307588364",
        firstDateOfPublication: "2022-08-12",
        referenceLink: "http://example.com/horror2",
        categoryId: 2,
        categoryName: "Horror",
        recommendedBookId: 202,
        liked: false,
        rating: 4,
        read: false,
        lastUpdatedTime: Int64(Date().timeIntervalSince1970)
    ))
}
