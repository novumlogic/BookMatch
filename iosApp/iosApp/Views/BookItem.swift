//
//  BookItem.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 15/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct BookItem: View{
    let book: BookDetails
    @State private var liked: KotlinBoolean?
    var onLikeDislike: (KotlinInt, String, KotlinBoolean?) -> Void
    init(book: BookDetails, onLikeDislike : @escaping (KotlinInt, String, KotlinBoolean?) -> Void ) {
        self.book = book
        _liked = State(initialValue: book.liked)
        self.onLikeDislike = onLikeDislike
    }
     
            var body: some View{
        
        VStack {
            
            ImageHolder(urlString: "\(Constants.shared.OPEN_LIBRARY_API_URL)/\(book.isbn)-L.jpg",frameWidth: nil, frameHeight: nil, topPadding: -10, cornerRadius: 25,maxWidth: .infinity, maxHeight: .infinity)
            
            Text(book.bookName)
                .font(.title2)
                .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, alignment: .leading)
                .lineLimit(2, reservesSpace: true)
                .multilineTextAlignment(.leading)
                .padding(.horizontal, 10)
            
            Text(book.authorName)
                .font(.subheadline)
                .frame(maxWidth: /*@START_MENU_TOKEN@*/.infinity/*@END_MENU_TOKEN@*/, alignment: .leading)
                .padding(.horizontal, 10)
                        
            LikeDislikeButton(book: book, like: $liked, onLikeDislike: onLikeDislike)
            
        }
        .frame(width: 250, height: 380)
        .overlay(
            RoundedRectangle(cornerRadius: 25.0)
                .stroke(Color.theme.outlineVariant, lineWidth: 2)
        )
        .padding(.leading,10)
        .padding(.vertical,4)
    }
}




#Preview {
    BookItem(book:BookDetails(
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
    )) { _,_,_ in
        
    }
}
