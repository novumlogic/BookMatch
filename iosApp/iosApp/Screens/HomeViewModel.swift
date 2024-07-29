//
//  HomeViewModel.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 22/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

final class HomeViewModel: ObservableObject{
    @Published var likedBookDict: [Int: String] = [:]
    @Published var dislikedBookDict: [Int: String] = [:]
    //recommendedBookId -> (BookName, Rating) 
    @Published var ratingDict: [Int: (String,Int)] = [:]
    
    private let remoteDataSource = RemoteDataSource.shared
    
    func changeLikeDislike(recommendedBookId: Int32, value: Bool?, currentTimeMillis: Int64) {
        Task {
            do{
                let status = try await remoteDataSource.changeLikeDislike(recommendedBookId: recommendedBookId, value: value != nil ? KotlinBoolean(bool: value!) : nil, currentTimeMillis: currentTimeMillis)
                if status == true {
                    print("changeLikeDislike: \(recommendedBookId) Updated successfully to \(String(describing: value))")
                } else {
                    print("changeLikeDislike: Update failed for \(recommendedBookId) and value = \(String(describing: value))")
                }
            }catch{
                print("Error changeLikeDislike: \(error)")
            }
        }
    }
    
    func updateRatings(recommendedBookId: Int, rating: Int?, currentTimeMillis: Int64) {
        Task {
            do{
                let finalRating = rating == 0 ? nil : rating
                let status = try await remoteDataSource.updateRatings(recommendedBookId: Int32(recommendedBookId), rating: (rating != nil) ? KotlinInt(int: Int32(rating!)) : nil, currentTimeMillis: currentTimeMillis)
                if status == true {
                    print("updateRatings: \(recommendedBookId) = \(String(describing: finalRating)) successfully")
                } else {
                    print("updateRatings: failed for \(recommendedBookId) and value = \(String(describing: finalRating))")
                }
            }catch{
                print("Error updateRatings: \(error)")
            }
        }
    }
    
    func updateReadStatus(recommendedBookId: Int, status: Bool, currentTimeMillis: Int64) {
        Task {
            do{
                let result = try await remoteDataSource.updateReadStatus(recommendBookId: Int32(recommendedBookId), status: status, currentTimeMillis: currentTimeMillis)
                if result == true {
                    print("updateReadStatus: \(recommendedBookId) = \(status) successfully")
                } else {
                    print("updateReadStatus: \(recommendedBookId) = \(status) failed")
                }
            }catch{
                print("Error updateReadStatus: \(error)")
            }
        }
    }
}
