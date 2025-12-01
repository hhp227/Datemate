//
//  MainView.swift
//  DateMate
//
//  Created by 홍희표 on 2021/12/03.
//

import SwiftUI

struct MainView: View {
    @StateObject private var viewModel = MainViewModel()
    
    @State private var selectedTab = 0
    
    let onNavigateToSubFirst: (String) -> Void
    
    let onNavigateToSubSecond: () -> Void
    
    let onNavigateToMyProfile: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            // Content
            // TODO

            // Main Tab Content
            TabView(selection: $selectedTab) {
                DiscoverView(onNavigateToSubFirst: onNavigateToSubFirst)
                    .tabItem {
                        Label("탐색", systemImage: "house")
                    }
                    .tag(0)
                LoungeView(onNavigateToSubSecond: onNavigateToSubSecond)
                    .tabItem {
                        Label("라운지", systemImage: "list.bullet")
                    }
                    .tag(1)
                FavoriteView()
                    .tabItem {
                        Label("관심", systemImage: "heart")
                    }
                    .tag(2)
                ChatRoomView()
                    .tabItem {
                        Label("채팅", systemImage: "bubble.left")
                    }
                    .tag(3)
            }
        }
        .navigationTitle("Datemate")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: onNavigateToMyProfile) {
                    Image(systemName: "heart")
                }
            }
        }
    }
}

/*struct MainView2: View {
    @State private var selectedTab = 0
    
    var onNavigate: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            // Content
            // TODO

            // Main Tab Content
            TabView(selection: $selectedTab) {
                FirstView {
                    onNavigate("sub_first/Test")
                }
                .tabItem {
                    Label("First", systemImage: "house")
                }
                .tag(0)

                SecondView {
                    onNavigate("sub_second")
                }
                .tabItem {
                    Label("Second", systemImage: "heart")
                }
                .tag(1)
            }
        }
        .navigationTitle("Datemate")
        .navigationBarTitleDisplayMode(.inline)
    }
}*/

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView(
            onNavigateToSubFirst: { _ in },
            onNavigateToSubSecond: {},
            onNavigateToMyProfile: {}
        )
    }
}
