//
//  FirstView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/12.
//

import SwiftUI

struct LoungeView2: View {
    var onNavigateToSubFirst: (String) -> Void

    var body: some View {
        VStack(spacing: 16) {
            Button("Next") {
                onNavigateToSubFirst("Test")
            }
            Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                .padding(.horizontal)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }
}

struct LoungeView2_Previews: PreviewProvider {
    static var previews: some View {
        LoungeView2(onNavigateToSubFirst: { _ in })
    }
}
