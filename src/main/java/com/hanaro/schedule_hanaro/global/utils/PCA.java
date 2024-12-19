package com.hanaro.schedule_hanaro.global.utils;

import org.apache.commons.math3.linear.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PCA {
    private RealVector mean;
    private RealMatrix components;
    private double[] singularValues;

    /**
     * SVD를 사용하여 차원을 축소합니다.
     */
    public Map<String, RealVector> fit(Map<String, RealVector> vectors, int targetDimension) {
        // 벡터를 행렬로 변환
        RealMatrix data = vectorsToMatrix(vectors);

        // 데이터 스케일링 및 중심화
        this.mean = calculateMean(data);
        RealMatrix centeredData = centerData(data, this.mean);

        try {
            // SVD 수행
            SingularValueDecomposition svd = new SingularValueDecomposition(centeredData);
            this.singularValues = svd.getSingularValues();

            // 특이값을 이용한 설명된 분산 비율 계산
            printVarianceRatio();

            // V 행렬에서 주성분 추출
            RealMatrix V = svd.getV();
            if (V == null) {
                throw new IllegalStateException("SVD 결과에서 V가 null입니다.");
            }
            int k = Math.min(targetDimension, V.getColumnDimension());
            this.components = V.getSubMatrix(0, V.getRowDimension() - 1, 0, k - 1);

            // 차원 축소된 데이터 계산
            Map<String, RealVector> reducedData = matrixToVectors(centeredData.multiply(this.components), new ArrayList<>(vectors.keySet()));

            // 소수점 제한 적용
            return formatVectors(reducedData, 2);

        } catch (Exception e) {
            System.err.println("SVD 계산 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 새로운 벡터에 대해 동일한 PCA 변환을 적용합니다.
     */
    public RealVector transform(RealVector vector) {
        if (vector == null) {
            throw new IllegalArgumentException("입력 벡터가 null입니다.");
        }
        if (this.mean == null || this.components == null) {
            throw new IllegalStateException("PCA가 아직 학습되지 않았습니다. fit 메소드를 먼저 호출하세요.");
        }
        // 중심화
        RealVector centeredVector = vector.subtract(this.mean);
        // PCA 변환 적용
        RealVector transformedVector = this.components.transpose().operate(centeredVector);
        // 소수점 제한 적용
        return formatVector(transformedVector, 6);
    }

    /**
     * 설명된 분산 비율을 출력합니다.
     */
    private void printVarianceRatio() {
        double totalVariance = 0.0;
        for (double value : this.singularValues) {
            totalVariance += value * value;
        }

        System.out.println("설명된 분산 비율:");
        double cumulativeVariance = 0.0;
        for (int i = 0; i < Math.min(5, this.singularValues.length); i++) {
            double varianceRatio = (this.singularValues[i] * this.singularValues[i]) / totalVariance;
            cumulativeVariance += varianceRatio;
            System.out.printf("주성분 %d: %.2f%% (누적: %.2f%%)%n",
                    i + 1, varianceRatio * 100, cumulativeVariance * 100);
        }
    }

    /**
     * 벡터들을 행렬로 변환합니다.
     */
    private static RealMatrix vectorsToMatrix(Map<String, RealVector> vectors) {
        // 입력 벡터의 차원과 개수 확인
        int n_samples = vectors.size();
        int n_features = vectors.values().iterator().next().getDimension();

        // 행렬 생성
        double[][] data = new double[n_samples][n_features];
        int i = 0;
        for (RealVector vector : vectors.values()) {
            data[i++] = vector.toArray();
        }

        return MatrixUtils.createRealMatrix(data);
    }

    /**
     * 데이터의 평균을 계산합니다.
     */
    private static RealVector calculateMean(RealMatrix data) {
        int n_features = data.getColumnDimension();
        double[] meanArray = new double[n_features];

        for (int j = 0; j < n_features; j++) {
            meanArray[j] = Arrays.stream(data.getColumn(j)).average().orElse(0.0);
        }

        return MatrixUtils.createRealVector(meanArray);
    }

    /**
     * 데이터를 중심화합니다.
     */
    private static RealMatrix centerData(RealMatrix data, RealVector mean) {
        RealMatrix centered = data.copy();
        int n_samples = centered.getRowDimension();

        for (int i = 0; i < n_samples; i++) {
            centered.setRowVector(i, centered.getRowVector(i).subtract(mean));
        }

        return centered;
    }

    /**
     * 행렬을 벡터 맵으로 변환합니다.
     */
    private static Map<String, RealVector> matrixToVectors(RealMatrix matrix, List<String> keys) {
        Map<String, RealVector> result = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            result.put(keys.get(i), matrix.getRowVector(i));
        }
        return result;
    }

    /**
     * 벡터를 소수점 제한된 형태로 변환합니다.
     */
    private static RealVector formatVector(RealVector vector, int decimalPlaces) {
        double[] formattedArray = Arrays.stream(vector.toArray())
                .map(value -> Math.round(value * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces))
                .toArray();
        return MatrixUtils.createRealVector(formattedArray);
    }

    /**
     * 벡터 맵의 모든 벡터에 소수점 제한을 적용합니다.
     */
    private static Map<String, RealVector> formatVectors(Map<String, RealVector> vectors, int decimalPlaces) {
        Map<String, RealVector> formattedVectors = new HashMap<>();
        vectors.forEach((key, vector) -> formattedVectors.put(key, formatVector(vector, decimalPlaces)));
        return formattedVectors;
    }
}
