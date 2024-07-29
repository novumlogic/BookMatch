//
//  Utils.swift
//  iosApp
//
//  Created by NovumLogic-MacMiniM1 on 18/07/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

enum FetchSource {
    case supabase
    case ai
}

enum RecommendationStatus : Equatable{
    case loading
    case loaded
    case failed
}

enum FailedReason{
    case noInternet, aiError, none
}
