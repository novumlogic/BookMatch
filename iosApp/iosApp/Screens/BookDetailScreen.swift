//
//  BookDetailScreenb.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 05/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

private let list =  [
    BookDetails(
        bookName: "Thriller Book 1",
        authorName: "Author A",
        genreTags: ["Thriller", "Mystery"],
        description: "Description of Thriller Book 1",
        pages: "350",
        isbn: "1234567890",
        firstDateOfPublication: "2020-01-01",
        referenceLink: "http://example.com/thriller1",
        categoryId: 1,
        categoryName: "Thriller",
        recommendedBookId: 101,
        liked: true,
        rating: 5,
        read: true,
        lastUpdatedTime: Int64(Date().timeIntervalSince1970)
    ),
    BookDetails(
        bookName: "Thriller Book 2",
        authorName: "Author B",
        genreTags: ["Thriller", "Suspense"],
        description: "Description of Thriller Book 2",
        pages: "400",
        isbn: "0987654321",
        firstDateOfPublication: "2019-06-15",
        referenceLink: "http://example.com/thriller2",
        categoryId: 1,
        categoryName: "Thriller",
        recommendedBookId: 102,
        liked: false,
        rating: 4,
        read: false,
        lastUpdatedTime: Int64(Date().timeIntervalSince1970)
    ),
    BookDetails(
        bookName: "Thriller Book 3",
        authorName: "Author C",
        genreTags: ["Thriller", "Crime"],
        description: "Description of Thriller Book 3",
        pages: "300",
        isbn: "1122334455",
        firstDateOfPublication: "2021-11-23",
        referenceLink: "http://example.com/thriller3",
        categoryId: 1,
        categoryName: "Thriller",
        recommendedBookId: 103,
        liked: true,
        rating: 3,
        read: true,
        lastUpdatedTime: Int64(Date().timeIntervalSince1970)
    )
]


struct BookDetailScreen: View {
    
    @Binding var book: BookDetails
    var bookList: [BookDetails]
    var remainingBookList: [BookDetails] {
        bookList.filter{ $0 != book}
    }
    
    @State var readStatus : Int = 1
    @State var rating: Int?
    
    //passing recommendedBookId,  read value
    let onRead: (Int, Bool) -> Void
    //passing recommendedBookId, book name , rating value
    let onRatingChange: (Int,String, Int?) -> Void
    
    @State private var showRecommendationProcessAlert = false
    @State private var showReadBeforeRateAlert = false
    
    
    var body: some View {

        NavigationView {
            ScrollView{
                ImageHolder(urlString: "\(Constants.shared.OPEN_LIBRARY_API_URL)/\(book.isbn)-L.jpg", frameWidth: 250, frameHeight: 250, topPadding: nil, cornerRadius: nil, maxWidth: nil, maxHeight: nil)
                    .aspectRatio(CGSize(width:1, height: 1), contentMode: .fit)
                    .padding()
    
                Text(book.bookName)
                    .font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
                    .multilineTextAlignment(.center)
                Text(book.authorName)
                
                
                ReadStatusButton(readStatus: readStatus){ readStatus in
                    if let recommendedBookId = book.recommendedBookId {
                        self.readStatus = readStatus
                        
                        if(readStatus == 1){
                            self.rating = nil
                            onRatingChange(recommendedBookId.intValue, book.bookName, nil)
                        }
                        
                        onRead(recommendedBookId.intValue ,readStatus == 0 ? true : false)
                        
                    } else{
                        self.showRecommendationProcessAlert = true
                    }
                }
                RatingBar(rating: rating){ rating in
                    
                    if readStatus == 1 {
                        self.showReadBeforeRateAlert = true
                    }else{
                        
                        if let recommendedBookId = book.recommendedBookId {
                            self.rating = rating
                            onRatingChange(recommendedBookId.intValue, book.bookName ,rating)
                        } else{
                            self.showRecommendationProcessAlert = true
                        }
    
                    }
                }
                
                HStack {
                    ForEach(book.genreTags, id: \.self) {genre in
                        Text(genre)
                            .padding()
                            .background(Color.theme.secondaryContainer)
                            .clipShape(RoundedRectangle(cornerRadius: 12.0), style: FillStyle())
                    }
                }
                
                Text(book.description_)
                    .multilineTextAlignment(.center)
                    .padding()
                
                if !remainingBookList.isEmpty {
                    Divider()
                    
                    Text("Recommendation based on your books")
                        .foregroundStyle(Color.theme.primaryColor)
                        .padding()
                    HorizontalBookList(list: remainingBookList, bookToShow: $book)
                    
                }
                
            }
        }
        .alert("Try again in a moment the recommendation is being processed", isPresented: $showRecommendationProcessAlert) {
            Button("OK", role: .cancel) {
                showRecommendationProcessAlert = false
            }
        }  
        .alert("Change read status before rating ", isPresented: $showReadBeforeRateAlert) {
            Button("OK", role: .cancel) {
                showReadBeforeRateAlert = false
            }
        }
        .onAppear{
            readStatus = book.read ? 0 : 1
            rating = book.rating?.intValue
        }
        
     
    }
}

struct HorizontalBookList: View{
    let list: [BookDetails]
    @Binding var bookToShow: BookDetails
    var body: some View{
            ScrollView(.horizontal, showsIndicators: false){
                HStack{
                    ForEach(list, id: \.self){
                        book in
                        Button{
                            bookToShow = book
                        } label:{
                            HorizontalBookItem(book: book)
                        }
                            
                    }
                }
            }.padding(/*@START_MENU_TOKEN@*/10/*@END_MENU_TOKEN@*/)
        }
}





#Preview {
    BookDetailScreen(book: .constant(BookDetails(
        bookName: "Horror Book 3",
        authorName: "Author F",
        genreTags: ["Horror", "Psychological", "hi"],
        description: "This is multiline description. This is multiline description. This is multiline description.This is multiline description.",
        pages: "280",
        isbn: "9780307588364",
        firstDateOfPublication: "2017-03-14",
        referenceLink: "http://example.com/horror3",
        categoryId: 2,
        categoryName: "Horror",
        recommendedBookId: 203,
        liked: true,
        rating: 3,
        read: true,
        lastUpdatedTime: Int64(Date().timeIntervalSince1970)
    )), bookList:  [
        BookDetails(
            bookName: "Thriller Book 1Thriller Book 1Thriller Book 1",
            authorName: "Author AAuthor A",
            genreTags: ["Thriller", "Mystery"],
            description: "Description of Thriller Book 1",
            pages: "350",
            isbn: "9780312924584",
            firstDateOfPublication: "2020-01-01",
            referenceLink: "http://example.com/thriller1",
            categoryId: 1,
            categoryName: "Thriller",
            recommendedBookId: 101,
            liked: true,
            rating: 5,
            read: true,
            lastUpdatedTime: Int64(Date().timeIntervalSince1970)
        ),
        BookDetails(
            bookName: "Thriller Book 2",
            authorName: "Author B",
            genreTags: ["Thriller", "Suspense"],
            description: "Description of Thriller Book 2",
            pages: "400",
            isbn: "9780307588364",
            firstDateOfPublication: "2019-06-15",
            referenceLink: "http://example.com/thriller2",
            categoryId: 1,
            categoryName: "Thriller",
            recommendedBookId: 102,
            liked: false,
            rating: 4,
            read: false,
            lastUpdatedTime: Int64(Date().timeIntervalSince1970)
        ),
        BookDetails(
            bookName: "Thriller Book 3",
            authorName: "Author C",
            genreTags: ["Thriller", "Crime"],
            description: "Description of Thriller Book 3",
            pages: "300",
            isbn: "9780307588364",
            firstDateOfPublication: "2021-11-23",
            referenceLink: "http://example.com/thriller3",
            categoryId: 1,
            categoryName: "Thriller",
            recommendedBookId: 103,
            liked: true,
            rating: 3,
            read: true,
            lastUpdatedTime: Int64(Date().timeIntervalSince1970)
        )
    ], onRead: {_,_ in }) { _, _, _ in
        
    }
}
