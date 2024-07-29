//
//  LikeDislikeButton.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 15/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct LikeDislikeButton : View{
    let book: BookDetails
    @Binding var like: KotlinBoolean?
    @State private var showAlert = false
    //recommendedBookId, bookName, likeStatus value being passed upwards
    let onLikeDislike: (KotlinInt, String, KotlinBoolean?) -> Void
    
    
    var body: some View{
        HStack{
           Button(action: {
               guard let id = book.recommendedBookId else {
                   showAlert = true
                   return }
               
               if(like != false){
                   like = false
               }else{
                   like = nil
               }
               onLikeDislike(id, book.bookName, like)
               
           }, label: {
               if let like {
                   
                   if(like == false){
                       Image("DislikeFillLogo")
                           .resizable()
                           .frame(width: 24,height: 24)

                   }else{
                       Image("DislikeLogo")
                           .resizable()
                           .frame(width: 24,height: 24)

                   }
                   
               } else {
                   Image("DislikeLogo")
                       .resizable()
                       .frame(width: 24,height: 24)
               }
               
            })
           .font(.title2)
           .frame(maxWidth: .infinity,minHeight: 50,alignment: .center)
            
            Button(action: {
                guard let id = book.recommendedBookId else {
                    showAlert = true
                    return }
               
                if(like != true){
                    like = true
                }else{
                    like = nil
                }
                
                onLikeDislike(id, book.bookName, like)
                
            }, label: {
                if let like {
                    
                    if(like == true){
                        Image("LikeFillLogo")
                            .resizable()
                            .frame(width: 24,height: 24)

                    }else{
                        Image("LikeLogo")
                            .resizable()
                            .frame(width: 24,height: 24)

                    }
                    
                } else{
                    Image("LikeLogo")
                        .resizable()
                        .frame(width: 24,height: 24)
                }
                
            })
            .font(.title2)
            .frame( maxWidth: .infinity,minHeight: 50,alignment: .center)
            
            .alert("Try again in a moment the recommendation is being processed", isPresented: $showAlert) {
                Button("OK", role: .cancel) {
                    self.showAlert = false 
                }
            }
            
                        
        }
    }
}


#Preview {
    LikeDislikeButton(book:BookDetails(
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
    ),like: .constant(true), onLikeDislike: { _, _, _ in
        
    })
        .frame(width: 180)
}
